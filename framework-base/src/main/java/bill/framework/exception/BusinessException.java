package bill.framework.exception;

import bill.framework.enums.ResponseCode;
import bill.framework.enums.SysResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class BusinessException extends RuntimeException {

    /** 业务异常码，对应 ResponseCode.code */
    private String code;

    /** HTTP 状态码 */
    private int httpStatus;

    /** 构造函数：自定义 code、messageKey、httpStatus、占位符参数 */
    public BusinessException(String code, String messageKey, int httpStatus) {
        super(messageKey);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    /** 构造函数：自定义 code、messageKey，默认 HTTP 1000 */
    public BusinessException(String code, String messageKey) {
        this(code, messageKey, SysResponseCode.BUSINESS_ERROR.getHttpStatus());
    }

    /** 构造函数：自定义 code、messageKey 和 cause，默认 HTTP 1000 */
    public BusinessException(String code, String messageKey, Throwable cause) {
        this(code, messageKey, SysResponseCode.BUSINESS_ERROR.getHttpStatus());
        log.error(messageKey, cause);
    }

    /** 构造函数：仅 messageKey，默认 code 和 HTTP 1000 */
    public BusinessException(String messageKey) {
        this(SysResponseCode.BUSINESS_ERROR.getCode(), messageKey, SysResponseCode.BUSINESS_ERROR.getHttpStatus());
    }


    /** 构造函数：使用 ResponseCode 枚举，默认不带占位符参数 */
    public BusinessException(ResponseCode statusEnum) {
        this(statusEnum.getCode(), statusEnum.getMsg(), statusEnum.getHttpStatus());
    }

    /** 构造函数：使用 ResponseCode 枚举，带自定义 messageKey */
    public BusinessException(ResponseCode statusEnum, String messageKey) {
        this(statusEnum.getCode(), messageKey, statusEnum.getHttpStatus());
    }

    /** 构造函数：使用 ResponseCode 枚举，带 Throwable cause */
    public BusinessException(ResponseCode statusEnum, Throwable cause) {
        this(statusEnum.getCode(), statusEnum.getMsg(), statusEnum.getHttpStatus());
        log.error(statusEnum.getMsg(), cause);
    }

}
