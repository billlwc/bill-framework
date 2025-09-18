package com.bill.test.service;

import bill.framework.web.log.RequestLog;
import bill.framework.web.log.RequestLogConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class DefaultRequestLogConsumer implements RequestLogConsumer {

    @Override
    public Set<String> excludePaths() {
        return Set.of("/list","/");
    }

    @Override
    public void consume(RequestLog logInfo) {
        // 可以选择写日志
       log.info("RequestLogConsumer收到请求日志:{}", logInfo);

    }
}