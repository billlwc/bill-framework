package com.bill.test.entity;

import bill.framework.web.reply.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;


import java.io.Serial;
import java.time.LocalTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wencai
 * @since 2025-08-25
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("store")
@Schema(name = "Store", description = "")
public class Store extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("store_name")
    private String storeName;

    @Schema(description = "门店地址")
    @TableField("store_address")
    private String storeAddress;

    @TableField("store_logo")
    private String storeLogo;

    @TableField("store_country")
    private Long storeCountry;

    @Schema(description = "门店联系人")
    @TableField("store_contacts")
    private String storeContacts;

    @Schema(description = "门店联系电话")
    @TableField("store_contact_number")
    private String storeContactNumber;

    @Schema(description = "开始营业时间")
    @TableField("business_hours_from")
    private LocalTime businessHoursFrom;

    @Schema(description = "结束营业时间")
    @TableField("business_hours_till")
    private LocalTime businessHoursTill;

    @Schema(description = "门店状态")
    @TableField("store_status")
    private String storeStatus;

    @Schema(description = "门店编号")
    @TableField("store_code")
    private String storeCode;

    @Schema(description = "邮编")
    @TableField("zip_code")
    private String zipCode;
}
