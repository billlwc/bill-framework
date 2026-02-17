package bill.framework.redis.idempotent;

import bill.framework.enums.SysResponseCode;
import bill.framework.exception.BusinessException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotentAspect {

    private static final String IDEMPOTENT_PREFIX = "idempotent:";

    private final RedisTemplate<String, Object> redisTemplates;

    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(bill.framework.redis.idempotent.Idempotent)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Idempotent idempotent = method.getAnnotation(Idempotent.class);

        String key = buildKey(idempotent, joinPoint, signature);

        // SET NX：原子操作，key 不存在则设置成功（返回 true），已存在则失败（返回 false）
        Boolean success = redisTemplates.opsForValue()
                .setIfAbsent(key, "1", idempotent.timeout(), idempotent.timeUnit());

        if (!Boolean.TRUE.equals(success)) {
            log.warn("幂等拦截: key={}", key);
            throw new BusinessException(SysResponseCode.CONFLICT, idempotent.msg());
        }

        log.debug("幂等锁定: key={}, timeout={}{}",
                key, idempotent.timeout(), idempotent.timeUnit());
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            // 执行异常时释放 key，允许重新提交
            redisTemplates.delete(key);
            throw e;
        } finally {
            if (idempotent.deleteOnSuccess()) {
                redisTemplates.delete(key);
            }
        }
    }

    /**
     * 构建幂等 key：
     * 1. 指定 key（SpEL） → idempotent:{customKey}
     * 2. 默认             → idempotent:{userId/ip}:{类名:方法名}:{参数MD5}
     */
    private String buildKey(Idempotent idempotent, ProceedingJoinPoint joinPoint, MethodSignature signature) {
        if (StrUtil.isNotBlank(idempotent.key())) {
            String customKey = parseSpEL(idempotent.key(), joinPoint.getArgs(), signature);
            return IDEMPOTENT_PREFIX + customKey;
        }

        Method method = signature.getMethod();
        String methodPath = method.getDeclaringClass().getSimpleName() + ":" + method.getName();
        String identity = getIdentity();
        String paramsMd5 = buildParamsMd5(joinPoint.getArgs());

        return IDEMPOTENT_PREFIX + identity + ":" + methodPath + ":" + paramsMd5;
    }

    /**
     * 获取请求者身份：已登录取用户ID，未登录取 IP
     */
    private String getIdentity() {
        try {
            if (StpUtil.isLogin()) {
                return "user:" + StpUtil.getLoginId();
            }
        } catch (Exception ignored) {}
        return "ip:" + getClientIp();
    }

    /**
     * 对方法参数做 MD5，生成参数指纹
     */
    private String buildParamsMd5(Object[] args) {
        if (args == null || args.length == 0) {
            return "no-params";
        }
        try {
            String paramsJson = JSONUtil.toJsonStr(args);
            return DigestUtil.md5Hex(paramsJson);
        } catch (Exception e) {
            return "serialize-error";
        }
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "UNKNOWN";
            HttpServletRequest request = attrs.getRequest();
            String[] headers = {"X-Forwarded-For", "X-Real-IP", "CF-Connecting-IP", "Proxy-Client-IP"};
            for (String header : headers) {
                String ip = request.getHeader(header);
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    return ip.contains(",") ? ip.split(",")[0].trim() : ip;
                }
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /**
     * 解析 SpEL 表达式
     */
    private String parseSpEL(String expression, Object[] args, MethodSignature signature) {
        if (!expression.contains("#") && !expression.contains("+")) {
            return expression;
        }
        String[] paramNames = signature.getParameterNames();
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        String value = parser.parseExpression(expression).getValue(context, String.class);
        return Objects.requireNonNullElse(value, "null");
    }
}
