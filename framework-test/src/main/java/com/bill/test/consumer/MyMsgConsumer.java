package com.bill.test.consumer;

import bill.framework.redis.message.RedisMsgConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class MyMsgConsumer implements RedisMsgConsumer {
    @Override
    public void redisMessage(String message) {
        log.info("MyMsg收到消息啦:{}", message);
    }

    @Override
    public String redisTopic() {
        return "MyMsg";
    }

    @Override
    public boolean queue() {
        return false;
    }
}
