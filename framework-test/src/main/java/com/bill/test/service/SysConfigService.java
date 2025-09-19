package com.bill.test.service;

import bill.framework.redis.message.RedisMsgConsumer;
import bill.framework.web.log.MethodLog;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bill.test.entity.SysConfig;
import com.bill.test.mapper.SysConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wencai
 * @since 2025-08-22
 */
@Service
@Slf4j
public class SysConfigService extends ServiceImpl<SysConfigMapper, SysConfig> implements RedisMsgConsumer {


    @Transactional(readOnly = true)
    public void processConfigs() {
        try (Cursor<SysConfig> cursor = getBaseMapper().selectCursor(Wrappers.emptyWrapper())) {
            int count = 0;
            for (SysConfig config : cursor) {
                count++;
                log.info("第{}条数据 [{}] - 时间: {}", count, config.getConfigName(), System.currentTimeMillis());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @MethodLog(title = "测试一下",message = "哈哈哈哈")
    public String test(String string) {
        return "这是测试数据";
    }


    @Override
    public void redisMessage(String message) {
        log.info("sysConfig消费到消息啦:{}", message);
    }

    @Override
    public String redisTopic() {
        return "sysConfig";
    }

    @Override
    public boolean queue() {
        return true;
    }


}
