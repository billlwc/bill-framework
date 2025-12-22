package bill.framework.web.log;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MethodLogAspect {

    @Value("${spring.application.name:unknown-app}")
    private String appName;

    @Autowired(required = false)
    private LogConsumer logConsumer;


    @Around("@annotation(bill.framework.web.log.MethodLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startMillis = System.currentTimeMillis();
        LocalDateTime startTime = LocalDateTime.now();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        MethodLog methodLogAnno = method.getAnnotation(MethodLog.class);
        String paramsJson = "[]";
        String path = method.getDeclaringClass().getName() + "#" + method.getName();
        try {
            // 方法参数序列化
            Object[] args = joinPoint.getArgs();
            paramsJson = JSONUtil.toJsonStr(args);
        } catch (Exception e) {
            log.warn("{}:方法日志参数序列化失败",path);
        }
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        }  finally {
            long duration = System.currentTimeMillis() - startMillis;
            LocalDateTime endTime = LocalDateTime.now();
            String responseJson = "null";
            try {
                responseJson = JSONUtil.toJsonStr(result);
            } catch (Exception e) {
                log.warn("{}:方法日志返回值序列化失败",path);
            }
            if (logConsumer != null) {
                MethodLogInfo methodLog = MethodLogInfo.builder()
                        .title(methodLogAnno.value())
                        .message(methodLogAnno.message())
                        .appName(appName)
                        .path(path)
                        .params(paramsJson)
                        .response(responseJson)
                        .startTime(startTime)
                        .endTime(endTime)
                        .durationMs(duration)
                        .traceId(MDC.get("traceId"))
                        .build();

                if (StpUtil.isLogin()) {
                    methodLog.setUserId((String) StpUtil.getLoginId());
                }

                logConsumer.methodLog(methodLog);
            }
        }
    }

}