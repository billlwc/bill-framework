package bill.framework.web.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {



    @Bean(name = "virtual")
    public Executor getVirtualExecutor() {
        // 使用虚拟线程，并且命名，便于日志和调试
        ThreadFactory threadFactory = Thread.ofVirtual().name("async-virtual-", 0).factory();
        return runnable -> {
            String traceId = MDC.get("traceId");
            threadFactory.newThread(() -> {
                try {
                    if (traceId != null) MDC.put("traceId", traceId);
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            }).start();
        };
    }

    @Bean(name = "async")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        // 核心：继承父线程 MDC
        executor.setTaskDecorator(runnable -> {
            String traceId = MDC.get("traceId");
            return () -> {
                try {
                    if (traceId != null) MDC.put("traceId", traceId);
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        });
        executor.initialize();
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return getVirtualExecutor();

    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        // 更完善的异常日志
        return (ex, method, params) -> {
            log.error("Async method '{}' threw an exception with params: {}",
                    method.getName(), Arrays.toString(params), ex);
        };
    }
}
