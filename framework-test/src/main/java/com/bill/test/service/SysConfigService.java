package com.bill.test.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bill.test.entity.SysConfig;
import com.bill.test.mapper.SysConfigMapper;
import jodd.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

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
public class SysConfigService extends ServiceImpl<SysConfigMapper, SysConfig>  {


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




}
