package bill.framework.redis.message;

public interface RedisMsgConsumer {

    void redisMessage(String message);

    String redisTopic();

    //是否支持延时
    boolean redisDelay();
}
