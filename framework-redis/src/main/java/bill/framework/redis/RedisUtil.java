package bill.framework.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类，基于 {@link RedisTemplate} 封装常用操作
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplates;

    // ============================ 通用操作 ============================

    /**
     * 设置 key 的过期时间
     *
     * @param key  缓存 key
     * @param time 过期时间（秒）
     * @return 设置是否成功
     */
    public boolean expire(String key, long time) {
        return redisTemplates.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 获取 key 的剩余过期时间
     *
     * @param key 缓存 key
     * @return 剩余时间（秒），返回 0 表示永久有效
     */
    public long getExpire(String key) {
        return redisTemplates.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断 key 是否存在
     *
     * @param key 缓存 key
     * @return true 存在，false 不存在
     */
    public boolean hasKey(String key) {
        return redisTemplates.hasKey(key);
    }

    /**
     * 删除一个或多个 key
     *
     * @param key 可变参数，支持传入多个 key
     */
    public void del(String... key) {
        if (key != null) {
            for (String k : key) {
                redisTemplates.delete(k);
            }
        }
    }

    // ============================ String ============================

    /**
     * 获取字符串缓存
     *
     * @param key 缓存 key
     * @return 对应的值，若不存在则返回 null
     */
    public Object get(String key) {
        return key == null ? null : redisTemplates.opsForValue().get(key);
    }

    /**
     * 设置字符串缓存
     *
     * @param key   缓存 key
     * @param value 缓存值
     */
    public void set(String key, Object value) {
        redisTemplates.opsForValue().set(key, value);
    }

    /**
     * 设置字符串缓存并指定过期时间
     *
     * @param key   缓存 key
     * @param value 缓存值
     * @param time  过期时间（秒），小于等于 0 表示无限期
     */
    public void set(String key, Object value, long time) {
        if (time > 0) {
            redisTemplates.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } else {
            set(key, value);
        }
    }

    /**
     * 递增
     *
     * @param key   缓存 key
     * @param delta 增加的值（必须大于 0）
     * @return 增加后的值
     */
    public Long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplates.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   缓存 key
     * @param delta 减少的值（必须大于 0）
     * @return 减少后的值
     */
    public Long burst(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplates.opsForValue().increment(key, -delta);
    }

    // ============================ Hash ============================

    /**
     * 获取 Hash 中的指定项
     *
     * @param key  Hash 的 key
     * @param item Hash 的项
     * @return 对应的值
     */
    public Object hget(String key, String item) {
        return redisTemplates.opsForHash().get(key, item);
    }

    /**
     * 获取整个 Hash
     *
     * @param key Hash 的 key
     * @return Hash 的所有键值对
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplates.opsForHash().entries(key);
    }

    /**
     * 批量设置 Hash
     *
     * @param key Hash 的 key
     * @param map 键值对
     */
    public void hmset(String key, Map<String, Object> map) {
        redisTemplates.opsForHash().putAll(key, map);
    }

    /**
     * 批量设置 Hash，并设置过期时间
     *
     * @param key  Hash 的 key
     * @param map  键值对
     * @param time 过期时间（秒）
     * @return 是否成功
     */
    public Boolean hmset(String key, Map<String, Object> map, long time) {
        redisTemplates.opsForHash().putAll(key, map);
        return time <= 0 || expire(key, time);
    }

    /**
     * 设置 Hash 中的某一项
     *
     * @param key   Hash 的 key
     * @param item  项
     * @param value 值
     */
    public void hset(String key, String item, Object value) {
        redisTemplates.opsForHash().put(key, item, value);
    }

    /**
     * 设置 Hash 中的某一项并指定过期时间
     *
     * @param key   Hash 的 key
     * @param item  项
     * @param value 值
     * @param time  过期时间（秒）
     */
    public void hset(String key, String item, Object value, long time) {
        redisTemplates.opsForHash().put(key, item, value);
        if (time > 0) {
            expire(key, time);
        }
    }

    /**
     * 删除 Hash 中的项
     *
     * @param key  Hash 的 key
     * @param item 可变参数，可以删除多个项
     */
    public void hdel(String key, Object... item) {
        redisTemplates.opsForHash().delete(key, item);
    }

    /**
     * 判断 Hash 中是否存在某项
     *
     * @param key  Hash 的 key
     * @param item 项
     * @return true 存在，false 不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplates.opsForHash().hasKey(key, item);
    }

    /**
     * Hash 递增
     *
     * @param key  Hash 的 key
     * @param item 项
     * @param by   增加的值（必须大于 0）
     * @return 增加后的值
     */
    public double hincr(String key, String item, double by) {
        return redisTemplates.opsForHash().increment(key, item, by);
    }

    /**
     * Hash 递减
     *
     * @param key  Hash 的 key
     * @param item 项
     * @param by   减少的值（必须大于 0）
     * @return 减少后的值
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplates.opsForHash().increment(key, item, -by);
    }

    // ============================ ZSet ============================

    /**
     * 添加 ZSet 元素
     *
     * @param key   ZSet 的 key
     * @param id    元素值
     * @param score 分值
     * @return 是否成功
     */
    public Boolean addZSet(String key, String id, double score) {
        return redisTemplates.opsForZSet().add(key, id, score);
    }

    /**
     * 获取 ZSet 中某元素的分值
     *
     * @param key ZSet 的 key
     * @param id  元素值
     * @return 分值
     */
    public Double getZSetScore(String key, String id) {
        return redisTemplates.opsForZSet().score(key, id);
    }

    /**
     * 获取 ZSet 中指定范围的元素（升序）
     */
    public Set<Object> getZSetRange(String key, int start, int end) {
        return redisTemplates.opsForZSet().range(key, start, end);
    }

    /**
     * 获取 ZSet 中指定范围的元素（降序，排行榜）
     */
    public Set<Object> getZSetReverseRange(String key, int start, int end) {
        return redisTemplates.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取分值区间内的元素（升序）
     */
    public Set<Object> getZSetRangeByScore(String key, double min, double max) {
        return redisTemplates.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 获取分值区间内的元素（降序）
     */
    public Set<Object> getZSetReverseRangeByScore(String key, double min, double max) {
        return redisTemplates.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * 删除 ZSet 中的指定元素
     */
    public Long getZSetDel(String key, String id) {
        return redisTemplates.opsForZSet().remove(key, id);
    }

    // ============================ Set ============================

    /**
     * 获取 Set 中的所有值
     */
    public Set<Object> sGet(String key) {
        return redisTemplates.opsForSet().members(key);
    }

    /**
     * 判断 Set 中是否存在某值
     */
    public Boolean sHasKey(String key, Object value) {
        return redisTemplates.opsForSet().isMember(key, value);
    }

    /**
     * 向 Set 添加一个或多个值
     */
    public Long sSet(String key, Object... values) {
        return redisTemplates.opsForSet().add(key, values);
    }

    /**
     * 向 Set 添加一个或多个值并设置过期时间
     */
    public Long sSetAndTime(String key, long time, Object... values) {
        Long count = redisTemplates.opsForSet().add(key, values);
        if (time > 0) expire(key, time);
        return count;
    }

    /**
     * 获取 Set 长度
     */
    public Long sGetSetSize(String key) {
        return redisTemplates.opsForSet().size(key);
    }

    /**
     * 移除 Set 中的一个或多个值
     */
    public Long setRemove(String key, Object... values) {
        return redisTemplates.opsForSet().remove(key, values);
    }

    // ============================ List ============================

    /**
     * 获取 List 指定区间的元素
     */
    public List<Object> lGet(String key, long start, long end) {
        return redisTemplates.opsForList().range(key, start, end);
    }

    /**
     * 获取 List 长度
     */
    public Long lGetListSize(String key) {
        return redisTemplates.opsForList().size(key);
    }

    /**
     * 根据索引获取 List 中的元素
     */
    public Object lGetIndex(String key, long index) {
        return redisTemplates.opsForList().index(key, index);
    }

    /**
     * 向 List 添加元素（右侧）
     */
    public Long lSet(String key, Object value) {
        return redisTemplates.opsForList().rightPush(key, value);
    }

    /**
     * 向 List 添加元素并设置过期时间
     */
    public void lSet(String key, Object value, long time) {
        redisTemplates.opsForList().rightPush(key, value);
        if (time > 0) expire(key, time);
    }

    /**
     * 向 List 批量添加元素
     */
    public Long lSet(String key, List<Object> value) {
        return redisTemplates.opsForList().rightPushAll(key, value);
    }

    /**
     * 向 List 批量添加元素并设置过期时间
     */
    public void lSet(String key, List<Object> value, long time) {
        redisTemplates.opsForList().rightPushAll(key, value);
        if (time > 0) expire(key, time);
    }

    /**
     * 修改 List 中指定索引的元素
     */
    public void lUpdateIndex(String key, long index, Object value) {
        redisTemplates.opsForList().set(key, index, value);
    }

    /**
     * 移除 List 中的指定元素
     *
     * @param key   缓存 key
     * @param count 移除的数量
     * @param value 要移除的值
     * @return 实际移除的数量
     */
    public Long lRemove(String key, long count, Object value) {
        return redisTemplates.opsForList().remove(key, count, value);
    }

    // ============================ Pub/Sub ============================

    /**
     * 发布消息
     *
     * @param channel 消息通道
     * @param message 消息内容
     */
    public void convertAndSend(String channel, Object message) {
        redisTemplates.convertAndSend(channel, message);
    }

}
