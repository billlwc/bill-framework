package com.bill.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 用户更新 DTO - 分组校验示例
 */
@Data
@Schema(description = "用户更新请求")
public class UserUpdateDTO {

    /**
     * 校验分组：更新基本信息
     */
    public interface UpdateBasic {}

    /**
     * 校验分组：更新密码
     */
    public interface UpdatePassword {}

    @NotNull(message = "用户ID不能为空", groups = {UpdateBasic.class, UpdatePassword.class})
    @Schema(description = "用户ID", example = "1")
    private Long id;

    @NotBlank(message = "用户名不能为空", groups = UpdateBasic.class)
    @Size(min = 3, max = 20, message = "用户名长度必须在 3-20 个字符之间", groups = UpdateBasic.class)
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Email(message = "邮箱格式不正确", groups = UpdateBasic.class)
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @NotBlank(message = "旧密码不能为空", groups = UpdatePassword.class)
    @Schema(description = "旧密码", example = "oldPassword123")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空", groups = UpdatePassword.class)
    @Size(min = 6, max = 32, message = "新密码长度必须在 6-32 个字符之间", groups = UpdatePassword.class)
    @Schema(description = "新密码", example = "newPassword123")
    private String newPassword;
}
