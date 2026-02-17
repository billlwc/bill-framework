package bill.framework.redis.limit;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式限流注解（基于 Redisson 令牌桶算法）
 * <p>
 * 使用示例：
 * <pre>
 * // 每秒最多 10 次请求（接口维度）
 * {@literal @}RateLimit(value = 10)
 *
 * // 每分钟每个 IP 最多 60 次请求
 * {@literal @}RateLimit(value = 60, time = 1, timeUnit = TimeUnit.MINUTES, type = RateLimitType.IP)
 *
 * // 每秒每个用户最多 5 次请求
 * {@literal @}RateLimit(value = 5, type = RateLimitType.USER, msg = "操作过于频繁，请稍后再试")
 *
 * // 自定义 key（SpEL 表达式）
 * {@literal @}RateLimit(value = 1, time = 60, key = "'sms:' + #phone")
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 时间窗口内允许的最大请求数，默认 100
     */
    long value() default 100;

    /**
     * 时间窗口大小，默认 1
     */
    long time() default 1;

    /**
     * 时间单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 自定义限流 key，支持 SpEL 表达式
     * 不填则根据 type 自动生成（类名:方法名）
     * 示例：key = "'sms:' + #phone"
     */
    String key() default "";

    /**
     * 限流类型，默认接口维度
     */
    RateLimitType type() default RateLimitType.DEFAULT;

    /**
     * 超限时的提示信息，支持国际化 key，默认使用系统提示
     */
    String msg() default "";
}
