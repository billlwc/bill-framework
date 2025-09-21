package com.bill.test.controller;

import bill.framework.thread.VirtualThreadMdcExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.concurrent.*;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Service
public class UserInfo implements Serializable {
    private Integer id;
    private String username;
    private String password;

    @Async
    public void test( ) {
        log.info("@Async日志");
    }

    @SneakyThrows
    @Async("async")
    public void test2( ) {
        try (ExecutorService executor = VirtualThreadMdcExecutor.newVirtualThreadPerTaskExecutor()) {
            Future<Boolean> future= executor.submit(this::test3);
            future.get();
        }
        log.info("@Virtual日志");
    }

    public boolean test3( ){
        log.info("@Virtual日志1");
        return true;
    }
}
