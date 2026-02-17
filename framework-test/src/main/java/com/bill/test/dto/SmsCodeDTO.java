package com.bill.test.dto;

import com.bill.test.validation.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 短信发送 DTO - 自定义校验注解示例
 */
@Data
@Schema(description = "短信发送请求")
public class SmsCodeDTO {

    @NotBlank(message = "手机号不能为空")
    @Phone(message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "验证码类型（register-注册, login-登录, reset-重置密码）", example = "register")
    private String type;
}
