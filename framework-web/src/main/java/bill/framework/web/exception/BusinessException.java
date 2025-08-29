package bill.framework.web.exception;

import bill.framework.web.enums.ResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class BusinessException extends RuntimeException {
    private String code;
    private String url;
    private int httpStatus;

    public BusinessException(String code, String message, int httpStatus){
        super(message);
        this.code = code;
        this.httpStatus=httpStatus;
    }

    public BusinessException(String code, String message){
        this(code,message, ResponseCode.ERROR_SYSTEM.getHttpStatus());
    }
    public BusinessException(String code, String message, Throwable cause){
        this(code,message, ResponseCode.ERROR_SYSTEM.getHttpStatus());
        log.error(message,cause);
    }

    public BusinessException(String message){
        this(ResponseCode.ERROR_SYSTEM.getCode(),message, ResponseCode.ERROR_SYSTEM.getHttpStatus());
    }

    public BusinessException(ResponseCode message){
        this(message.getCode(),message.getMessage(),message.getHttpStatus());
    }

    public BusinessException(ResponseCode statusEnum, String message){
        this(statusEnum.getCode(),message,statusEnum.getHttpStatus());
    }

    public BusinessException(ResponseCode message, Throwable cause){
        this(message.getCode(),message.getMessage(),message.getHttpStatus());
        log.error(message.getMessage(),cause);
    }

}
