package bill.framework.redis.idempotent;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性注解 - 防止重复提交
 * <p>
 * 原理：基于 Redis SET NX，在有效期内同一请求只能执行一次。
 * <p>
 * key 生成规则（优先级从高到低）：
 * 1. 指定了 key（SpEL 表达式） → 使用自定义 key
 * 2. 未指定 key → 使用 用户ID（未登录则取 IP）+ 类名:方法名 + 参数 MD5
 * <p>
 * 使用示例：
 * <pre>
 * // 5 秒内不能重复提交
 * {@literal @}Idempotent
 * {@literal @}PostMapping("/order/submit")
 * public Order submit(@RequestBody OrderDTO dto) { ... }
 *
 * // 自定义提示和有效期
 * {@literal @}Idempotent(timeout = 10, msg = "订单正在处理，请勿重复提交")
 * public void pay(@RequestBody PayDTO dto) { ... }
 *
 * // 自定义 key（SpEL 表达式）
 * {@literal @}Idempotent(key = "'order:' + #dto.orderId", timeout = 30)
 * public void processOrder(OrderDTO dto) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 幂等有效期，默认 5 秒
     */
    long timeout() default 5;

    /**
     * 时间单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 自定义 key，支持 SpEL 表达式
     * 不填则自动根据用户 + 方法 + 参数生成
     */
    String key() default "";

    /**
     * 重复提交时的提示信息（支持国际化 key）
     */
    String msg() default "请勿重复提交";

    /**
     * 执行成功后是否立即删除 key（允许成功后再次提交）
     * true  → 成功后立即解锁，可再次提交
     * false → 等待 timeout 自然过期（默认）
     */
    boolean deleteOnSuccess() default false;
}
