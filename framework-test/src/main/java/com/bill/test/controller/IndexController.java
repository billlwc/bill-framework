package com.bill.test.controller;

import bill.framework.enums.SysResponseCode;
import bill.framework.exception.BusinessException;
import bill.framework.redis.RedisUtil;
import bill.framework.redis.limit.RateLimit;
import bill.framework.redis.limit.RateLimitType;
import bill.framework.redis.lock.RedisLock;
import bill.framework.redis.lock.RedisLockUtil;
import bill.framework.thread.ExecutorsMdcVirtual;
import bill.framework.web.annotation.ApiVersion;
import bill.framework.web.annotation.NoToken;
import bill.framework.web.bo.RequestPageBO;
import bill.framework.web.log.MethodLog;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bill.test.entity.SysConfig;
import com.bill.test.execption.BizException;
import com.bill.test.service.AiService;
import com.bill.test.service.SysConfigService;
import com.bill.test.service.UserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "测试")
public class IndexController {

    public final RedisUtil redisUtil;

    public final RedisLockUtil redisLock;

    public final SysConfigService sysConfigService;

    private final UserInfo userInfo;

    private final AiService aiService;
    


    @Operation(summary = "ai")
    @GetMapping("/ai")
    @NoToken
    public String question( String question)  {
       return aiService.askQuestion(question);
    }


    @Operation(summary = "登录")
    @PostMapping("/login")
    @NoToken
    public String index(@RequestBody UserInfo userInfo) {
        return UserUtils.login(userInfo.getId(),userInfo);
    }

    @Operation(summary = "信息")
    @GetMapping("/info/{id}")
    @Cacheable(value = "info:#60") //缓存
    public UserInfo info(@PathVariable String id)  {
        UserInfo userInfo= (UserInfo) UserUtils.getSession();
        log.info("userInfo1:{}", userInfo);
        redisUtil.set("userInfo",userInfo);
        userInfo= (UserInfo) redisUtil.get("userInfo",userInfo);
        log.info("userInfo2:{}", userInfo);

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
    @Cacheable(value = "list:#5") //缓存
    @MethodLog(value = "测试日志",message = "我的日志")
    public IPage<SysConfig> list(@ParameterObject RequestPageBO bo) {
        LambdaQueryWrapper queryWrapper= Wrappers.<SysConfig>lambdaQuery()
                .eq(bo.getId()!=null,SysConfig::getId,bo.getId());
        return sysConfigService.page(bo.getPage(),queryWrapper);
    }

    private String execute(SysConfig order) {
        return order.getConfigValue();
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
    @RedisLock(value = "'ss:'+#userInfo.getId()",msg = "稍等")
    public String s1(@ParameterObject UserInfo userInfo) {
        if(userInfo.getId().equals(new BigInteger("1"))){
            throw new BusinessException("锁了");
        }
        return "s1";
    }

    @Operation(summary = "锁2")
    @GetMapping("/s2")
    @NoToken
    public String s2(@ParameterObject UserInfo userInfo) {
        RLock rLock= redisLock.tryLock("ss:"+userInfo.getId(),false,10000,TimeUnit.MILLISECONDS);

        if(userInfo.getId().equals(new BigInteger("1"))){
            throw new BusinessException("锁了");
        }

        redisLock.releaseLock(rLock);
        return "s2";
    }


    @Operation(summary = "测试")
    @GetMapping("/test")
    //@Cacheable(value = "test:#60",key ="#sysConfig.id" ) //缓存
    @NoToken
    @MethodLog("123")
    public String test(@ParameterObject SysConfig sysConfig) throws ExecutionException, InterruptedException {
       // return sysConfigService.getOne(Wrappers.<SysConfig>lambdaQuery().le(SysConfig::getId,2).last("order by create_time desc limit 1"));
        String configValue= """
                {"min":0,"max":200000}
                """;
        log.info("configValue:{}", configValue);
        // 子线程
        ExecutorService executor = ExecutorsMdcVirtual.newVirtualThreadPerTaskExecutor();
        executor.submit(() -> {
            log.info("线程池测试");
        });
        executor.shutdown();

        Thread.ofVirtual().start(() -> log.info("虚拟线程池测试"));
        ExecutorsMdcVirtual.start(() -> log.info("虚拟线程池测试1"));
        Future<String> future= ExecutorsMdcVirtual.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                log.info("ok");
                return "124";
            }
        });

        log.info(future.get());

        userInfo.test();
        userInfo.test2();
       // ExceptionUtil.exception(StrUtil.isNotEmpty(configValue),"报错了");
        return  configValue;
    }


    @SneakyThrows
    @Operation(summary = "测试消息")
    @GetMapping("/no/{id}")
    @NoToken
    public void no(@PathVariable BigInteger id) {
        log.info("id:{}", id);
        JSONObject jsonObject=new JSONObject();
        jsonObject.set("code","延时消息");
        //redisUtil.sendQueueMessage("sysConfig",jsonObject);
        //redisUtil.sendQueueMessage("MyMsg",jsonObject,3,TimeUnit.SECONDS);

        jsonObject.set("code","实时消息");
        redisUtil.sendQueueMessage("sysConfig",jsonObject);
        redisUtil.publishMessage("MyMsg",jsonObject);
    }


    @Operation(summary = "异常消息")
    @GetMapping("/error/{str}")
    @NoToken
    public String error(@PathVariable String str) {
        String configValue= """
                {"min":0,"max":200000}
                """;
       // ExceptionUtil.exception(true,MyResponseCode.MY_ERROR,ArraysUtil.array("哈哈哈"));
       // throw new BusinessException(MyResponseCode.MY_ERROR,ArraysUtil.array("哈哈哈"));
        //int f=1/0;
        throw new BizException(SysResponseCode.SYSTEM_ERROR.getCode(),"我的异常",500);
       // return str;
    }

    // ==================== 限流示例 ====================

    @Operation(summary = "限流示例1 - 全局接口限流（每秒10次）")
    @GetMapping("/rate/default")
    @NoToken
    @RateLimit(value = 10, time = 1, timeUnit = TimeUnit.SECONDS)
    public String rateDefault() {
        return "成功！全局接口限流：所有用户共享每秒10次";
    }

    @Operation(summary = "限流示例2 - 按IP限流（每分钟60次）")
    @GetMapping("/rate/ip")
    @NoToken
    @RateLimit(value = 60, time = 1, timeUnit = TimeUnit.MINUTES, type = RateLimitType.IP)
    public String rateIp() {
        return "成功！按IP限流：每个IP每分钟60次";
    }

    @Operation(summary = "限流示例3 - 按用户限流（每秒5次）")
    @GetMapping("/rate/user")
    @RateLimit(value = 5, time = 1, timeUnit = TimeUnit.SECONDS, type = RateLimitType.USER, msg = "操作过于频繁，请稍后再试")
    public String rateUser() {
        BigInteger userId = UserUtils.userId();
        return "成功！按用户限流：用户 " + userId + " 每秒最多5次";
    }

    @Operation(summary = "限流示例4 - 自定义key（SpEL表达式）")
    @GetMapping("/rate/custom/{action}")
    @NoToken
    @RateLimit(value = 3, time = 60, timeUnit = TimeUnit.SECONDS, key = "'action:' + #action", msg = "该操作每分钟最多3次")
    public String rateCustom(@PathVariable String action) {
        return "成功！自定义key限流：" + action + " 每分钟最多3次";
    }

    @Operation(summary = "限流示例5 - 发送短信（每个手机号每天10次）")
    @PostMapping("/rate/sms")
    @NoToken
    @RateLimit(value = 10, time = 1, timeUnit = TimeUnit.DAYS, key = "'sms:' + #phone", msg = "验证码发送过于频繁，每天最多10次")
    public String sendSms(@RequestParam String phone) {
        // 实际发送短信逻辑...
        return "验证码已发送到 " + phone;
    }


}
