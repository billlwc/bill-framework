package com.bill.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户注册 DTO - 基础参数校验示例
 */
@Data
@Schema(description = "用户注册请求")
public class UserRegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在 3-20 个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在 6-32 个字符之间")
    @Schema(description = "密码", example = "123456")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @NotNull(message = "年龄不能为空")
    @Min(value = 18, message = "年龄必须大于等于 18 岁")
    @Max(value = 120, message = "年龄必须小于等于 120 岁")
    @Schema(description = "年龄", example = "25")
    private Integer age;

    @Size(max = 200, message = "个人简介不能超过 200 个字符")
    @Schema(description = "个人简介", example = "这是一段个人简介")
    private String bio;
}
