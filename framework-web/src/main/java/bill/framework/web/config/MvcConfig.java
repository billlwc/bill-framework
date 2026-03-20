package bill.framework.web.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public abstract class MvcConfig implements WebMvcConfigurer {
    /**
     * 业务系统可选实现此方法，注册自己的拦截器
     */
    protected abstract void setInterceptors(InterceptorRegistry registry);


    protected abstract void setCorsMappings(CorsRegistry registry);

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 调用业务系统扩展
        setInterceptors(registry);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
       setCorsMappings(registry);
    }
}
