package bill.framework.redis.config;


import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.util.StringUtils;

import java.time.Duration;


/**
 * @Description: 对redis缓存进行扩展，使其可以在注解处指定key值过期时间<br/>
 *  使用方式：将过期时间拼接至value值后面，使用#号分割<br/>
 *  单位：秒<br/>
 *  列： @Cacheable(value="football:league#35",key = "#po")<br/>
 *  key为football:league的过期时间是35秒
 * @Author: Yangf
 * @Create: 2021-09-13 18:59
 */
@Slf4j
public class RedisCacheManagers extends RedisCacheManager {
    public RedisCacheManagers(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        String[] array = StringUtils.delimitedListToStringArray(name, "#");
        name = array[0];
        log.info("create redis cache: {}",name);
        if (array.length > 1 && NumberUtil.isNumber(array[1])) { // 解析TTL
            long ttl = Long.parseLong(array[1]);
            cacheConfig = cacheConfig.entryTtl(Duration.ofSeconds(ttl));
        }
        return super.createRedisCache(name, cacheConfig);
    }
}