package com.bill.test.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.Serializable;

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
    public void test() {
        log.info("测试日志");
    }
}
