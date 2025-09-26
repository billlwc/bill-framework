package bill.framework.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName ResultStatusEnum
 * @Description 系统异常枚举
 * @Author c
 * @Date 2020/7/14 9:12 上午
 * @Version V1.0
 **/
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ResponseCode {
    /* ========== 1xx：信息类 ========== */
    CONTINUE("CONTINUE", "sys_continue", 100),                       // 继续，请求尚未完成
    SWITCHING_PROTOCOLS("SWITCHING_PROTOCOLS", "sys_switching_protocols", 101), // 协议切换
    PROCESSING("PROCESSING", "sys_processing", 102),                  // 处理中

    /* ========== 2xx：成功类 ========== */
    SUCCESS("SUCCESS", "sys_success", 200),                           // 请求成功
    CREATED("CREATED", "sys_created", 201),                           // 资源已创建
    ACCEPTED("ACCEPTED", "sys_accepted", 202),                        // 请求已接受，正在处理
    NO_CONTENT("NO_CONTENT", "sys_no_content", 204),                  // 请求成功，无返回内容

    /* ========== 3xx：重定向类 ========== */
    MOVED_PERMANENTLY("MOVED_PERMANENTLY", "sys_moved_permanently", 301), // 资源已永久移动
    FOUND("FOUND", "sys_found", 302),                                 // 资源临时移动
    SEE_OTHER("SEE_OTHER", "sys_see_other", 303),                     // 查看其他资源
    NOT_MODIFIED("NOT_MODIFIED", "sys_not_modified", 304),            // 资源未修改
    TEMPORARY_REDIRECT("TEMPORARY_REDIRECT", "sys_temporary_redirect", 307), // 临时重定向
    PERMANENT_REDIRECT("PERMANENT_REDIRECT", "sys_permanent_redirect", 308), // 永久重定向

    /* ========== 4xx：客户端错误类 ========== */
    BAD_REQUEST("BAD_REQUEST", "sys_bad_request", 400),               // 请求参数错误或不完整
    UNAUTHORIZED("UNAUTHORIZED", "sys_unauthorized", 401),            // 未认证或 Token 无效
    FORBIDDEN("FORBIDDEN", "sys_forbidden", 403),                      // 已认证但无权限
    NOT_FOUND("NOT_FOUND", "sys_not_found", 404),                      // 资源不存在
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "sys_method_not_allowed", 405), // 请求方法不支持
    NOT_ACCEPTABLE("NOT_ACCEPTABLE", "sys_not_acceptable", 406),      // 请求无法满足要求
    REQUEST_TIMEOUT("REQUEST_TIMEOUT", "sys_request_timeout", 408),   // 请求超时
    CONFLICT("CONFLICT", "sys_conflict", 409),                         // 请求冲突，资源状态不一致
    GONE("GONE", "sys_gone", 410),                                     // 资源已被永久删除
    LENGTH_REQUIRED("LENGTH_REQUIRED", "sys_length_required", 411),   // 缺少 Content-Length 头
    PAYLOAD_TOO_LARGE("PAYLOAD_TOO_LARGE", "sys_payload_too_large", 413), // 请求实体过大
    URI_TOO_LONG("URI_TOO_LONG", "sys_uri_too_long", 414),            // URI 过长
    UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE", "sys_unsupported_media_type", 415), // 不支持的请求格式
    RANGE_NOT_SATISFIABLE("RANGE_NOT_SATISFIABLE", "sys_range_not_satisfiable", 416), // Range 请求范围无效
    EXPECTATION_FAILED("EXPECTATION_FAILED", "sys_expectation_failed", 417), // 预期结果失败
    TOO_MANY_REQUESTS("TOO_MANY_REQUESTS", "sys_too_many_requests", 429), // 请求过于频繁，已限流

    /* ========== 5xx：服务端错误类 ========== */
    ERROR_SYSTEM("ERROR_SYSTEM", "sys_error_system", 500),            // 系统内部错误
    NOT_IMPLEMENTED("NOT_IMPLEMENTED", "sys_not_implemented", 501),    // 接口未实现
    BAD_GATEWAY("BAD_GATEWAY", "sys_bad_gateway", 502),                // 网关错误
    ERROR_BUSY("ERROR_BUSY", "sys_system_busy", 503),                  // 系统繁忙，请稍后再试
    GATEWAY_TIMEOUT("GATEWAY_TIMEOUT", "sys_gateway_timeout", 504),   // 网关超时
    HTTP_VERSION_NOT_SUPPORTED("HTTP_VERSION_NOT_SUPPORTED", "sys_http_version_not_supported", 505), // 不支持的 HTTP 版本

    /* ========== 业务自定义扩展 ========== */
    BUSINESS_ERROR("BUSINESS_ERROR", "business_error", 10000), // 业务异常
    BUSINESS_VALIDATION_RULE_FAILED("BUSINESS_VALIDATION_RULE_FAILED", "sys_business_validation_rule_failed", 10001), // 业务校验未通过
    DUPLICATE_REQUEST("DUPLICATE_REQUEST", "sys_duplicate_request", 10002), // 重复请求
    DATA_INCONSISTENCY("DATA_INCONSISTENCY", "sys_data_inconsistency", 10003), // 数据不一致
    THIRD_PARTY_ERROR("THIRD_PARTY_ERROR", "sys_third_party_error", 10004), // 第三方系统错误
    OPERATION_NOT_ALLOWED("OPERATION_NOT_ALLOWED", "sys_operation_not_allowed", 10005), // 操作不允许
    RESOURCE_LOCKED("RESOURCE_LOCKED", "sys_resource_locked", 10006); // 资源被锁定，无法操作


    private String code;
    private String msg;
    private int httpStatus;

}
