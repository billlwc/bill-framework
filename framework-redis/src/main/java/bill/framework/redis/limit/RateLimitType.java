package bill.framework.redis.limit;

/**
 * 限流类型枚举
 */
public enum RateLimitType {

    /** 全局接口维度：所有请求共享同一计数器 */
    DEFAULT,

    /** 按客户端 IP 限流 */
    IP,

    /** 按登录用户 ID 限流 */
    USER
}
