package bill.framework.exception;

import bill.framework.enums.ResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class BusinessException extends RuntimeException {

    /** 业务异常码，对应 ResponseCode.code */
    private String code;

    /** 国际化消息占位符参数 */
    private Object[] args;

    /** HTTP 状态码 */
    private int httpStatus;

    /** 构造函数：自定义 code、messageKey、httpStatus、占位符参数 */
    public BusinessException(String code, String messageKey, int httpStatus, Object[] args) {
        super(messageKey);
        this.code = code;
        this.httpStatus = httpStatus;
        this.args = args;
    }

    /** 构造函数：自定义 code、messageKey，默认 HTTP 1000 */
    public BusinessException(String code, String messageKey) {
        this(code, messageKey, ResponseCode.BUSINESS_ERROR.getHttpStatus(), null);
    }

    /** 构造函数：自定义 code、messageKey 和 cause，默认 HTTP 1000 */
    public BusinessException(String code, String messageKey, Throwable cause) {
        this(code, messageKey, ResponseCode.BUSINESS_ERROR.getHttpStatus(), null);
        log.error(messageKey, cause);
    }

    /** 构造函数：仅 messageKey，默认 code 和 HTTP 1000 */
    public BusinessException(String messageKey) {
        this(ResponseCode.BUSINESS_ERROR.getCode(), messageKey, ResponseCode.BUSINESS_ERROR.getHttpStatus(), null);
    }

    /** 构造函数：使用 ResponseCode 枚举，默认不带占位符参数 */
    public BusinessException(ResponseCode statusEnum) {
        this(statusEnum.getCode(), statusEnum.getMsg(), statusEnum.getHttpStatus(), null);
    }

    /** 构造函数：使用 ResponseCode 枚举，带自定义 messageKey */
    public BusinessException(ResponseCode statusEnum, String messageKey) {
        this(statusEnum.getCode(), messageKey, statusEnum.getHttpStatus(), null);
    }

    /** 构造函数：使用 ResponseCode 枚举，带 Throwable cause */
    public BusinessException(ResponseCode statusEnum, Throwable cause) {
        this(statusEnum.getCode(), statusEnum.getMsg(), statusEnum.getHttpStatus(), null);
        log.error(statusEnum.getMsg(), cause);
    }

    /** 构造函数：使用 ResponseCode 枚举，带占位符参数 args */
    public BusinessException(ResponseCode statusEnum, Object[] args) {
        this(statusEnum.getCode(), statusEnum.getMsg(), statusEnum.getHttpStatus(), args);
    }

    /** 构造函数：使用 ResponseCode 枚举，带 messageKey 和 args */
    public BusinessException(ResponseCode statusEnum, String messageKey, Object[] args) {
        this(statusEnum.getCode(), messageKey, statusEnum.getHttpStatus(), args);
    }
}
