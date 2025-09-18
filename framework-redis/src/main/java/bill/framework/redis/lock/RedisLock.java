package bill.framework.redis.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解
 * 功能：
 * 1. 基于 RedisLock 实现分布式锁
 * 2. 支持自定义 key（SpEL 表达式）
 * 3. 支持自定义超时时间
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {

    /**
     * 锁的 key，支持 SpEL 表达式，例如：
     * key = "'user:' + #userId"
     */
    String key();

    /**
     * 锁超时时间，默认 10 秒
     */
    long timeout() default 10;

    /**
     * 时间单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 获取锁失败时的提示信息
     */
    String message() default "系统繁忙，请稍后再试";
}
