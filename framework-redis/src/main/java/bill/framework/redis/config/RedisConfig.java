package bill.framework.redis.config;

import bill.framework.redis.cache.RedisCacheManagers;
import bill.framework.redis.message.RedisMsgConsumer;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Configuration
@ComponentScan("bill.framework")
@EnableCaching
@Slf4j
public class RedisConfig implements ApplicationRunner {

    @Autowired(required = false)
    private List<RedisMsgConsumer> redisMsgConsumers;

    @Autowired
    private RedissonClient redissonClient;
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
     * 消息核心监听容器
     */
    @Bean
    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        if (redisMsgConsumers != null) {
            for (RedisMsgConsumer handler : redisMsgConsumers) {
                if(!handler.queue()) {
                    log.info("注册 Redis 监听器，监听 Topic: {}", handler.redisTopic());
                    MessageListenerAdapter adapter = new MessageListenerAdapter(handler, "redisMessage");
                    adapter.afterPropertiesSet(); // 关键
                    container.addMessageListener(adapter, new PatternTopic(handler.redisTopic()));
                }
            }
        }
        return container;
    }

    @PostConstruct
    public void initQueue() {
        // 使用单独线程阻塞监听队列
        if(redisMsgConsumers!=null){
            for (RedisMsgConsumer handler : redisMsgConsumers) {
                if(handler.queue()){
                  Thread.ofVirtual().start(() -> {
                      RBlockingQueue<Object> queue = redissonClient.getBlockingQueue(handler.redisTopic());
                      log.info("注册 Redis 队列，监听 Topic: {}", handler.redisTopic());
                      while (!Thread.currentThread().isInterrupted()) {
                          try {
                              Object message = queue.take(); // 阻塞等待新消息
                              handler.redisMessage(JSONUtil.toJsonStr(message));
                          } catch (InterruptedException e) {
                              Thread.currentThread().interrupt(); // 保留中断状态
                              log.warn("虚拟线程被中断，但继续等待消息", e);
                          } catch (Exception e) {
                              log.error("处理消息异常, topic={}", handler.redisTopic(), e);
                          }
                      }
                  });
                }
            }
        }
    }


    @Override
    public void run(ApplicationArguments args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("redis.txt"))))) {
            reader.lines().forEach(System.out::println);
        }
    }
}
