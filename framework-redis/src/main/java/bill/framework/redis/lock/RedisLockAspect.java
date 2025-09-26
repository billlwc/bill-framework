package bill.framework.redis.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedisLockAspect {

    private final RedisLockUtil redisLock;

    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 环绕通知：方法执行前加锁，执行完成释放锁
     */
    @Around("@annotation(bill.framework.redis.lock.RedisLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisLock tryLock = method.getAnnotation(RedisLock.class);
        String key = parseKey(tryLock.value(), joinPoint.getArgs(), signature);
        RLock rLock=null;
        try {
            rLock=redisLock.tryLock(key,tryLock.block(), tryLock.timeout(), tryLock.timeUnit(), tryLock.errorMsg());
            return joinPoint.proceed();
        } finally {
            if (rLock!=null) {
                redisLock.releaseLock(rLock);
            }
        }
    }


    /**
     * 解析注解中的 key
     * 1. 如果 key 是常量字符串，直接返回
     * 2. 如果包含 SpEL 表达式，执行解析
     */
    private String parseKey(String keyExpression, Object[] args, MethodSignature methodSignature) {
        // 如果 key 中不包含 SpEL 特殊符号，直接返回
        if (!keyExpression.contains("#") && !keyExpression.contains("+")) {
            return keyExpression;
        }

        // SpEL 上下文绑定方法参数
        String[] paramNames = methodSignature.getParameterNames();
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        // 解析 SpEL 表达式
        String value = parser.parseExpression(keyExpression).getValue(context, String.class);
        return Objects.requireNonNullElse(value, "null");
    }

}