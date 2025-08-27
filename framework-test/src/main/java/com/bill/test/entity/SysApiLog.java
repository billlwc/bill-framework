package com.bill.test.entity;

import bill.framework.web.reply.BaseEntity;
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
@TableName("sys_api_log")
@Schema(name="SysApiLog",description="日志")
public class SysApiLog extends BaseEntity{

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("api_res")
    private String apiRes;

    @TableField("api_req")
    private String apiReq;

    @TableField("api_url")
    private String apiUrl;

    @TableField("user_id")
    private Long userId;

    @TableField("order_id")
    private Long orderId;

    @TableField("api_name")
    private String apiName;
}
