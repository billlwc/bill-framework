package com.bill.test.controller;

import bill.framework.redis.lock.RedisLockUtil;
import bill.framework.redis.RedisUtil;
import bill.framework.redis.lock.RedisLock;
import bill.framework.web.annotation.ApiVersion;
import bill.framework.web.log.MethodLog;
import bill.framework.web.annotation.NoToken;
import bill.framework.web.bo.RequestPageBO;
import bill.framework.web.exception.ExceptionUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bill.test.entity.SysConfig;
import com.bill.test.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "测试")
public class IndexController {

    public final RedisUtil redisUtil;

    public final RedisLockUtil redisLock;

    public final SysConfigService sysConfigService;

    private final UserInfo userInfo;

    @Operation(summary = "登录")
    @PostMapping("/login")
    @NoToken
    public String index(@RequestBody UserInfo userInfo) {
        StpUtil.login(userInfo.getId());
        StpUtil.getSession().set("info", userInfo);
        return StpUtil.getTokenValue();
    }

    @Operation(summary = "信息")
    @GetMapping("/info/{id}")
    public UserInfo info(@PathVariable String id) {
        UserInfo userInfo= (UserInfo) StpUtil.getSession().get("info");
        log.info("userInfo:{}", userInfo);
        return userInfo;
    }

    @Operation(summary = "退出")
    @GetMapping("/loginOut")
    public void loginOut() {
        StpUtil.logout();
    }


    @Operation(summary = "分页")
    @GetMapping("/list")
    @NoToken
    @Cacheable(value = "list:#2") //缓存
    @MethodLog(title = "测试日志",message = "我的日志")
    public IPage<SysConfig> list(@ParameterObject RequestPageBO bo) {
        LambdaQueryWrapper queryWrapper= Wrappers.<SysConfig>lambdaQuery()
                .eq(bo.getId()!=null,SysConfig::getId,bo.getId());
         return  sysConfigService.page(bo.getPage(),queryWrapper);
    }

    @Operation(summary = "流式查询")
    @GetMapping("/cursor")
    @NoToken
    public void list() {
        sysConfigService.processConfigs();
    }

    @Operation(summary = "v1")
    @GetMapping("/v1")
    @NoToken
    public String v1() {
        return sysConfigService.test("1223");
    }

    @Operation(summary = "v1")
    @GetMapping("/v1")
    @ApiVersion(2)
    public String v2() {
        return "v2";
    }

    @Operation(summary = "锁1")
    @GetMapping("/s1")
    @NoToken
    @RedisLock(key = "ss",message = "请稍后")
    public String s1(@ParameterObject UserInfo userInfo) {
       // redisLock.tryLock("ss");
        ThreadUtil.sleep(10000);
       // redisLock.releaseLock("ss");
        return "s1";
    }

    @Operation(summary = "锁2")
    @GetMapping("/s2")
   // @RedisLockAnno(key = "'ss:'+#userInfo.getId()",message = "请稍后")
    @NoToken
    public String s2(@ParameterObject UserInfo userInfo) {
        redisLock.tryLock("ss");
        ThreadUtil.sleep(1000);
        redisLock.releaseLock("ss");
        return "s2";
    }


    @Operation(summary = "测试")
    @GetMapping("/test")
    //@Cacheable(value = "test:#60",key ="#sysConfig.id" ) //缓存
    @NoToken
    @MethodLog(title = "123",message = "123")
    public String test(@ParameterObject SysConfig sysConfig) {
       // return sysConfigService.getOne(Wrappers.<SysConfig>lambdaQuery().le(SysConfig::getId,2).last("order by create_time desc limit 1"));
        String configValue= """
                {"min":0,"max":200000}
                """;
        log.info("configValue:{}", configValue);
        // 子线程
        String traceId = MDC.get("traceId");
        Thread.ofVirtual().start(() -> {
           // MDC.put("traceId", traceId);
            log.info("虚拟线程日志");
        });

        new Thread(() -> {
            MDC.put("traceId", traceId);
            log.info("Thread线程日志");
        }).start();

        userInfo.test(traceId);
       // ExceptionUtil.exception(StrUtil.isNotEmpty(configValue),"报错了");
        return  configValue;
    }


}
