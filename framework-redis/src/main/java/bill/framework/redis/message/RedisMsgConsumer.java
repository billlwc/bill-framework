package bill.framework.redis.message;

public interface RedisMsgConsumer {

    void redisMessage(String message);

    String redisTopic();
}
