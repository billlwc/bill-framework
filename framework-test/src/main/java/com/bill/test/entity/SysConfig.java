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
 * @since 2025-08-22
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_config")
@Schema(name="SysConfig",description="系统参数")
public class SysConfig extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("config_type")
    private String configType;

    @TableField("config_name")
    private String configName;

    @TableField("config_value")
    private String configValue;

    @TableField("config_desc")
    private String configDesc;
}
