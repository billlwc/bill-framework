package bill.framework.redis.message;

public interface RedisMsgConsumer {

    void redisMessage(String message);

    String redisTopic();

    //是否是队列消息
    boolean queue();
}
