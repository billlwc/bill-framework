package bill.framework.web.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})//用于方法和类上
@Retention(RetentionPolicy.RUNTIME)//运行时有效
@Documented //标识这是个注解并应该被 javadoc工具记录
@Mapping //标识映射
public @interface ApiVersion {
    /**
     * 标识版本号
     */
    int value() default 1;//default 表示默认值
}
