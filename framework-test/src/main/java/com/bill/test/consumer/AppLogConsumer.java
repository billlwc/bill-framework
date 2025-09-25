package com.bill.test.consumer;

import bill.framework.web.log.LogConsumer;
import bill.framework.web.log.MethodLogInfo;
import bill.framework.web.log.RequestLogInfo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class AppLogConsumer implements LogConsumer {

    @Override
    public Set<String> excludePaths() {
        return Set.of("/list","/");
    }

    @Override
    @Async
    public void requestLog(RequestLogInfo requestLog) {
        MDC.put("traceId", requestLog.getTraceId());
        // 可以选择写日志
        log.info("【{}】-【{}】-耗时：{}ms：{}",requestLog.getHttpMethod(),requestLog.getPath(),requestLog.getDurationMs(), requestLog);
    }

    @Override
    @Async
    public void methodLog(MethodLogInfo methodLog) {
        MDC.put("traceId", methodLog.getTraceId());
        log.info("【{}】-【{}】-耗时：{}ms：{}",methodLog.getTitle(),methodLog.getMessage(),methodLog.getDurationMs(), methodLog);
    }
}