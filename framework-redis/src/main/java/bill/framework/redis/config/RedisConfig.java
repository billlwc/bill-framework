package bill.framework.redis.config;

import bill.framework.redis.cache.RedisCacheManagers;
import cn.hutool.json.JSONUtil;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@ComponentScan("bill.framework.redis")
@EnableCaching
public class RedisConfig {

    /**
     * 缓存管理器<br/>
     * 扩展RedisCache，默认TTL为60秒
     * @return CacheManager
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60))
                .computePrefixWith(cacheName -> "caching:" + cacheName);
        return new RedisCacheManagers(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory),
                defaultCacheConfig);
    }

    /**
     * 序列化
     * @param factory factory
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // key 使用字符串序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // value 使用 Hutool JSON 序列化
        RedisSerializer<Object> valueSerializer = new RedisSerializer<>() {
            @Override
            public byte[] serialize(Object value) {
                if (value == null) {
                    return new byte[0];
                }
                // 使用 Hutool JSON 序列化对象为字节数组
                return JSONUtil.toJsonStr(value).getBytes();
            }

            @Override
            public Object deserialize(byte[] bytes) {
                if (bytes == null || bytes.length == 0) {
                    return null;
                }
                // 反序列化成 String，再用 JSONUtil 转对象，需要在使用时手动转换类型
                return new String(bytes);
            }
        };

        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

}
