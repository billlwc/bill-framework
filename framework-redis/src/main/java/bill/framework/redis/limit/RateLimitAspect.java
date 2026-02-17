package bill.framework.redis.limit;

import bill.framework.enums.SysResponseCode;
import bill.framework.exception.BusinessException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedissonClient redissonClient;

    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(bill.framework.redis.limit.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        String key = buildKey(rateLimit, joinPoint, signature);
        boolean allowed = tryAcquire(key, rateLimit);

        if (!allowed) {
            String msg = StrUtil.isNotBlank(rateLimit.msg())
                    ? rateLimit.msg()
                    : SysResponseCode.TOO_MANY_REQUESTS.getMsg();
            log.warn("限流触发: key={}, type={}, limit={}/{}{}", key, rateLimit.type(),
                    rateLimit.value(), rateLimit.time(), rateLimit.timeUnit());
            throw new BusinessException(SysResponseCode.TOO_MANY_REQUESTS, msg);
        }

        return joinPoint.proceed();
    }

    /**
     * 基于 Redisson RRateLimiter（令牌桶）尝试获取令牌
     * trySetRate 在 key 已存在时会跳过，不影响已有速率配置
     */
    private boolean tryAcquire(String key, RateLimit rateLimit) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(
                RateType.OVERALL,
                rateLimit.value(),
                rateLimit.time(),
                toRateIntervalUnit(rateLimit.timeUnit())
        );
        return rateLimiter.tryAcquire(1);
    }

    /**
     * 构建限流 key：
     * 1. 有自定义 key（SpEL）→ ratelimit:{customKey}
     * 2. IP 维度           → ratelimit:ip:{ip}:{类名:方法名}
     * 3. USER 维度         → ratelimit:user:{userId}:{类名:方法名}
     * 4. 默认（方法维度）    → ratelimit:default:{类名:方法名}
     */
    private String buildKey(RateLimit rateLimit, ProceedingJoinPoint joinPoint, MethodSignature signature) {
        Method method = signature.getMethod();
        String methodPath = method.getDeclaringClass().getSimpleName() + ":" + method.getName();

        if (StrUtil.isNotBlank(rateLimit.key())) {
            String customKey = parseSpEL(rateLimit.key(), joinPoint.getArgs(), signature);
            return "ratelimit:" + customKey;
        }

        return switch (rateLimit.type()) {
            case IP   -> "ratelimit:ip:" + getClientIp() + ":" + methodPath;
            case USER -> "ratelimit:user:" + getLoginUserId() + ":" + methodPath;
            default   -> "ratelimit:default:" + methodPath;
        };
    }

    /**
     * 获取客户端真实 IP（兼容多层代理）
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
     * 获取当前登录用户 ID，未登录返回 "anonymous"
     */
    private String getLoginUserId() {
        try {
            if (StpUtil.isLogin()) {
                return StpUtil.getLoginId().toString();
            }
        } catch (Exception ignored) {}
        return "anonymous";
    }

    /**
     * 解析 SpEL 表达式，绑定方法参数
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

    /**
     * 将 Java TimeUnit 转换为 Redisson RateIntervalUnit
     */
    private RateIntervalUnit toRateIntervalUnit(TimeUnit timeUnit) {
        return switch (timeUnit) {
            case MILLISECONDS -> RateIntervalUnit.MILLISECONDS;
            case MINUTES      -> RateIntervalUnit.MINUTES;
            case HOURS        -> RateIntervalUnit.HOURS;
            case DAYS         -> RateIntervalUnit.DAYS;
            default           -> RateIntervalUnit.SECONDS;
        };
    }
}
