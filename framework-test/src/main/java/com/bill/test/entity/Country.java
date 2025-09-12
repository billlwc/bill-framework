package com.bill.test.entity;


import bill.framework.web.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;

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
@TableName("country")
@Schema(name = "Country", description = "")
public class Country extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("country_name")
    private String countryName;

    @Schema(description = "国家货币")
    @TableField("country_currency")
    private String countryCurrency;

    @Schema(description = "国家代号")
    @TableField("country_code")
    private String countryCode;

    @Schema(description = "国家手机号编号")
    @TableField("country_phone_code")
    private String countryPhoneCode;
}
