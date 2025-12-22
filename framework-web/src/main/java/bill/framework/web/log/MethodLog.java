package bill.framework.web.log;

import java.lang.annotation.*;


/**
 * 方法及日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodLog {

    /**
     * 日志标题
     */
    String value() default "";

    /**
     * 日志消息
     */
    String message() default "";
}
