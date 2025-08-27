package bill.framework.redis;
import lombok.RequiredArgsConstructor;
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
public class RedisLock {

    /**
     * Redisson 客户端，Spring Boot 注入
     */

    private final RedissonClient redisson;

    /**
     * 构建分布式锁的 key
     * @param key 原始锁名
     * @return 带前缀的锁名
     */
    public String getKey(String key) {
        return "lock:" + key;
    }
    /**
     * 获取 RLock 对象，可手动操作锁
     * @param lockName 锁名
     * @return RLock 对象
     */
    public RLock lock(String lockName) {
        return redisson.getLock(getKey(lockName));
    }

    /**
     * 判断锁是否被占用
     * @param lockName 锁名
     * @return true 表示锁已被占用，false 表示未被占用
     */
    public boolean isLock(String lockName) {
        RLock rLock = lock(lockName);
        return rLock.isLocked();
    }

    /**
     * 尝试加锁，带自定义超时时间（阻塞线程）
     *
     * @param key 锁名
     * @param timeout 超时时间
     * @param timeUnit 时间单位
     */
    public void tryLock(String key, long timeout, TimeUnit timeUnit) {
        // 获取 RLock 对象
        RLock rLock = lock(key);
        // 加锁并设置自动释放时间，防止死锁
        rLock.lock(timeout, timeUnit);
        log.info("加锁成功: {} by {}", key, Thread.currentThread().getName());
    }

    /**
     * 尝试加锁，使用默认超时时间 10 秒
     *
     * @param key 锁名

     */
    public void tryLock(String key) {
         tryLock(key, 10, TimeUnit.SECONDS);
    }

    /**
     * 释放锁
     *
     * @param key 锁名
     */
    public void releaseLock(String key) {
        //获取所对象
        RLock rLock = lock(key);
        // 判断当前线程是否持有锁
        if (rLock.isHeldByCurrentThread()) {
            rLock.unlock();
            log.info("解锁成功 {}, by {}", key, Thread.currentThread().getName());
        }
    }
}

