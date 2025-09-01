package bill.framework.web.config;

import bill.framework.web.annotation.NoToken;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Method;

@SuppressWarnings("NullableProblems")
@Slf4j
@Configuration
@ComponentScan("bill.framework.web")
@EnableAsync
public class WebConfig  implements WebMvcConfigurer {

    @Bean
    public Snowflake snowflake() {
        // 生成随机 workerId 和 datacenterId（0~31）
        long workerId = RandomUtil.randomInt(0, 31);
        long datacenterId = RandomUtil.randomInt(0, 31);
        log.info("Initialization Snowflake datacenterId:{} workerId:{}", datacenterId, workerId);
        return IdUtil.getSnowflake(workerId, datacenterId);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)  {
        registry.addInterceptor(new HandlerInterceptor() {

                    @Override
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
                        String traceId = request.getHeader("X-Trace-Id");
                        if (traceId == null || traceId.isEmpty()) {
                            traceId = IdUtil.fastSimpleUUID();
                        }
                        MDC.put("traceId", traceId);
                        HandlerMethod handlerMethod=(HandlerMethod)handler;
                        Method method=handlerMethod.getMethod();
                        if (method.isAnnotationPresent(NoToken.class)) {
                            return true;
                        }
                        StpUtil.checkLogin();
                        return true;
                    }

                    @Override
                    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
                        // 请求完成后清理，避免线程复用导致污染
                        MDC.clear();
                    }
                })
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/favicon.ico",
                        "/webjars/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/doc.html");
    }


}
