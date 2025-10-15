package com.bill.test.config;

import bill.framework.web.config.MvcConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
@Slf4j
public class MyConfig extends MvcConfig {
    @Override
    protected void setInterceptors(InterceptorRegistry registry) {

    }
}
