package bill.framework.web.exception;

import bill.framework.web.enums.ResponseStatusEnum;
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
        this(code,message, ResponseStatusEnum.ERROR_SYSTEM.getHttpStatus());
    }
    public BusinessException(String code, String message, Throwable cause){
        this(code,message, ResponseStatusEnum.ERROR_SYSTEM.getHttpStatus());
        log.error(message,cause);
    }

    public BusinessException(String message){
        this(ResponseStatusEnum.ERROR_SYSTEM.getCode(),message, ResponseStatusEnum.ERROR_SYSTEM.getHttpStatus());
    }

    public BusinessException(ResponseStatusEnum message){
        this(message.getCode(),message.getMessage(),message.getHttpStatus());
    }

    public BusinessException(ResponseStatusEnum statusEnum, String message){
        this(statusEnum.getCode(),message,statusEnum.getHttpStatus());
    }

    public BusinessException(ResponseStatusEnum message, Throwable cause){
        this(message.getCode(),message.getMessage(),message.getHttpStatus());
        log.error(message.getMessage(),cause);
    }

}
