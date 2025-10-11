package bill.framework.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

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
public enum SysResponseCode implements ResponseCode {
    /* ========== 1xx：信息类 ========== */
    CONTINUE("CONTINUE", "sys_continue", HttpStatus.CONTINUE.value()),                       // 继续，请求尚未完成
    SWITCHING_PROTOCOLS("SWITCHING_PROTOCOLS", "sys_switching_protocols", HttpStatus.SWITCHING_PROTOCOLS.value()), // 协议切换
    PROCESSING("PROCESSING", "sys_processing", HttpStatus.PROCESSING.value()),                 // 处理中

    /* ========== 2xx：成功类 ========== */
    SUCCESS("SUCCESS", "sys_success", HttpStatus.OK.value()),                                  // 请求成功
    CREATED("CREATED", "sys_created", HttpStatus.CREATED.value()),                             // 资源已创建
    ACCEPTED("ACCEPTED", "sys_accepted", HttpStatus.ACCEPTED.value()),                        // 请求已接受，正在处理
    NO_CONTENT("NO_CONTENT", "sys_no_content", HttpStatus.NO_CONTENT.value()),                 // 请求成功，无返回内容

    /* ========== 3xx：重定向类 ========== */
    MOVED_PERMANENTLY("MOVED_PERMANENTLY", "sys_moved_permanently", HttpStatus.MOVED_PERMANENTLY.value()), // 资源已永久移动
    FOUND("FOUND", "sys_found", HttpStatus.FOUND.value()),                                     // 资源临时移动
    SEE_OTHER("SEE_OTHER", "sys_see_other", HttpStatus.SEE_OTHER.value()),                     // 查看其他资源
    NOT_MODIFIED("NOT_MODIFIED", "sys_not_modified", HttpStatus.NOT_MODIFIED.value()),         // 资源未修改
    TEMPORARY_REDIRECT("TEMPORARY_REDIRECT", "sys_temporary_redirect", HttpStatus.TEMPORARY_REDIRECT.value()), // 临时重定向
    PERMANENT_REDIRECT("PERMANENT_REDIRECT", "sys_permanent_redirect", HttpStatus.PERMANENT_REDIRECT.value()), // 永久重定向

    /* ========== 4xx：客户端错误类 ========== */
    BAD_REQUEST("BAD_REQUEST", "sys_bad_request", HttpStatus.BAD_REQUEST.value()),             // 请求参数错误或不完整
    UNAUTHORIZED("UNAUTHORIZED", "sys_unauthorized", HttpStatus.UNAUTHORIZED.value()),       // 未认证或 Token 无效
    FORBIDDEN("FORBIDDEN", "sys_forbidden", HttpStatus.FORBIDDEN.value()),                    // 已认证但无权限
    NOT_FOUND("NOT_FOUND", "sys_not_found", HttpStatus.NOT_FOUND.value()),                    // 资源不存在
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "sys_method_not_allowed", HttpStatus.METHOD_NOT_ALLOWED.value()), // 请求方法不支持
    NOT_ACCEPTABLE("NOT_ACCEPTABLE", "sys_not_acceptable", HttpStatus.NOT_ACCEPTABLE.value()), // 请求无法满足要求
    REQUEST_TIMEOUT("REQUEST_TIMEOUT", "sys_request_timeout", HttpStatus.REQUEST_TIMEOUT.value()), // 请求超时
    CONFLICT("CONFLICT", "sys_conflict", HttpStatus.CONFLICT.value()),                        // 请求冲突，资源状态不一致
    GONE("GONE", "sys_gone", HttpStatus.GONE.value()),                                        // 资源已被永久删除
    LENGTH_REQUIRED("LENGTH_REQUIRED", "sys_length_required", HttpStatus.LENGTH_REQUIRED.value()), // 缺少 Content-Length 头
    PAYLOAD_TOO_LARGE("PAYLOAD_TOO_LARGE", "sys_payload_too_large", HttpStatus.PAYLOAD_TOO_LARGE.value()), // 请求实体过大
    URI_TOO_LONG("URI_TOO_LONG", "sys_uri_too_long", HttpStatus.URI_TOO_LONG.value()),        // URI 过长
    UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE", "sys_unsupported_media_type", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()), // 不支持的请求格式
    RANGE_NOT_SATISFIABLE("RANGE_NOT_SATISFIABLE", "sys_range_not_satisfiable", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()), // Range 请求范围无效
    EXPECTATION_FAILED("EXPECTATION_FAILED", "sys_expectation_failed", HttpStatus.EXPECTATION_FAILED.value()), // 预期结果失败
    TOO_MANY_REQUESTS("TOO_MANY_REQUESTS", "sys_too_many_requests", HttpStatus.TOO_MANY_REQUESTS.value()), // 请求过于频繁，已限流

    /* ========== 5xx：服务端错误类 ========== */
    SYSTEM_ERROR("SYSTEM_ERROR", "sys_error_system", HttpStatus.INTERNAL_SERVER_ERROR.value()), // 系统内部错误
    NOT_IMPLEMENTED("NOT_IMPLEMENTED", "sys_not_implemented", HttpStatus.NOT_IMPLEMENTED.value()), // 接口未实现
    BAD_GATEWAY("BAD_GATEWAY", "sys_bad_gateway", HttpStatus.BAD_GATEWAY.value()),              // 网关错误
    SYSTEM_BUSY("SYSTEM_BUSY", "sys_system_busy", HttpStatus.SERVICE_UNAVAILABLE.value()),       // 系统繁忙，请稍后再试
    GATEWAY_TIMEOUT("GATEWAY_TIMEOUT", "sys_gateway_timeout", HttpStatus.GATEWAY_TIMEOUT.value()), // 网关超时
    HTTP_VERSION_NOT_SUPPORTED("HTTP_VERSION_NOT_SUPPORTED", "sys_http_version_not_supported", HttpStatus.HTTP_VERSION_NOT_SUPPORTED.value()), // 不支持的 HTTP 版本

    /* ========== 业务自定义扩展 ========== */
    BUSINESS_ERROR("BUSINESS_ERROR", "business_error", HttpStatus.INTERNAL_SERVER_ERROR.value()); // 业务异常


    private  String code = "";
    private String msg = "";
    private int httpStatus = 0;


    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public int getHttpStatus() {
        return this.httpStatus;
    }
}
