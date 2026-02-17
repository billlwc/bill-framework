package com.bill.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单创建 DTO - 嵌套对象校验示例
 */
@Data
@Schema(description = "订单创建请求")
public class OrderCreateDTO {

    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名不能超过 50 个字符")
    @Schema(description = "收货人姓名", example = "张三")
    private String receiverName;

    @NotBlank(message = "收货人手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "收货人手机号", example = "13800138000")
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    @Size(max = 200, message = "收货地址不能超过 200 个字符")
    @Schema(description = "收货地址", example = "北京市朝阳区xxx街道xxx号")
    private String receiverAddress;

    @NotEmpty(message = "订单商品列表不能为空")
    @Size(min = 1, max = 20, message = "订单商品数量必须在 1-20 个之间")
    @Valid  // 嵌套校验：会对列表中的每个 OrderItemDTO 进行校验
    @Schema(description = "订单商品列表")
    private List<OrderItemDTO> items;

    @Size(max = 500, message = "订单备注不能超过 500 个字符")
    @Schema(description = "订单备注", example = "请尽快发货")
    private String remark;

    /**
     * 订单商品项 DTO
     */
    @Data
    @Schema(description = "订单商品项")
    public static class OrderItemDTO {

        @NotNull(message = "商品ID不能为空")
        @Schema(description = "商品ID", example = "1001")
        private Long productId;

        @NotBlank(message = "商品名称不能为空")
        @Size(max = 100, message = "商品名称不能超过 100 个字符")
        @Schema(description = "商品名称", example = "iPhone 15 Pro")
        private String productName;

        @NotNull(message = "商品价格不能为空")
        @DecimalMin(value = "0.01", message = "商品价格必须大于 0")
        @DecimalMax(value = "999999.99", message = "商品价格不能超过 999999.99")
        @Digits(integer = 6, fraction = 2, message = "价格格式不正确")
        @Schema(description = "商品价格", example = "7999.00")
        private BigDecimal price;

        @NotNull(message = "购买数量不能为空")
        @Min(value = 1, message = "购买数量至少为 1")
        @Max(value = 99, message = "购买数量不能超过 99")
        @Schema(description = "购买数量", example = "2")
        private Integer quantity;
    }
}
