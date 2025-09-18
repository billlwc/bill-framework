package bill.framework.web.log;

import java.lang.annotation.*;


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
public @interface MethodLog {

    /**
     * 获取锁失败时的提示信息
     */
    String title() default "title";

    /**
     * 获取锁失败时的提示信息
     */
    String message() default "message";
}
