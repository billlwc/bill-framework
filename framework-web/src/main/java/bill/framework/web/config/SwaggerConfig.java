package bill.framework.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author rongrong
 * @version 1.0
 * @description Swagger3配置
 * @date 2021/01/12 21:00
 */
@Configuration
public class SwaggerConfig {

    /**
     * Springdoc OpenAPI 主配置
     */
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI().info(info());
    }

    /**
     * 解决 springdoc 中 BeanFactory 类型检查问题（如果使用 Lombok 或 FactoryBean 出现注入异常）
     */
    @Bean
    public static BeanFactoryPostProcessor bypassFactoryBeanCheck() {
        return bf -> ((DefaultListableBeanFactory) bf)
                .setAllowRawInjectionDespiteWrapping(true);
    }

    /**
     * API 文档信息
     */
    private Info info() {
        return new Info()
                .title("接口文档")
                .description("分页参数：当前页：page；每页展示条数：size；分页从1开始")
                .version("1.0")
                .summary("接口文档说明");
    }
}