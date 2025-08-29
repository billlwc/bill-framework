package bill.framework.web.enums;

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
    SUCCESS("SUCCESS", "success",200),//请求成功
    UNAUTHORIZED("UNAUTHORIZED", "unauthorized",401),//token无效
    BAD_REQUEST("ERROR_SYSTEM","bad_request",403),//非法请求
    NOT_FOUND("ERROR_SYSTEM","not_found",404),//非法请求
    ERROR_SYSTEM("ERROR_SYSTEM", "error_system",500);//系统异常


    private String code;
    private String message;
    private int httpStatus;

}
