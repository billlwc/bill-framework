package bill.framework.redis.lock;
import bill.framework.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;



/**
 * 基于 Redisson 实现的可重入分布式锁
 * 功能：
 * 1. 支持尝试加锁（带超时时间/默认超时时间）
 * 2. 支持释放锁
 * 3. 支持检查锁是否被占用
 * 适用场景：
 * 分布式环境下多实例同时操作共享资源时，防止并发冲突
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLockUtil {

    /**
     * Redisson 客户端，Spring Boot 注入
     */

    private final RedissonClient redisson;

    private static final String LOCK_NAME = "lock:";

    /**
     * 获取 RLock 对象，可手动操作锁
     * @param lockName 锁名
     * @return RLock 对象
     */
    private RLock getLock(String lockName) {
        return redisson.getLock(LOCK_NAME+lockName);
    }


    /**
     * 尝试加锁，带自定义超时时间（非阻塞线程）
     *
     * @param key 锁名
     * @param timeout 超时时间
     * @param timeUnit 时间单位
     */
    @SneakyThrows
    public RLock tryLock(String key, long timeout, TimeUnit timeUnit, String errorMsg) {
        RLock rLock = getLock(key);
        if (!rLock.tryLock(0, timeout, timeUnit)) {
            throw new BusinessException(errorMsg); // 失败直接抛异常
        }
        log.info("加锁成功: {}", key);
        return rLock;
    }

    /**
     * 尝试加锁，带自定义超时时间（阻塞线程）
     *
     * @param key 锁名
     * @param timeout 超时时间
     * @param unit 时间单位
     */
    @SneakyThrows
    public RLock lock(String key, long timeout, TimeUnit unit) {
        RLock rLock = getLock(key);
        boolean success = rLock.tryLock(timeout, timeout, unit);
        if (!success) {
            throw new RuntimeException("获取锁超时: "+timeout+"_"+unit.name()+":" + key);
        }
        log.info("加锁成功: {}", key);
        return rLock;
    }

    /**
     * 释放锁
     *
     * @param rLock 锁名
     */
    public void releaseLock(RLock rLock) {
        if (rLock.isHeldByCurrentThread()) {
            rLock.unlock();
            log.info("解锁成功！ {}", Thread.currentThread().getName());
        }
    }
}

