package bill.framework.web.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class BaseEntity implements Serializable {

    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(value ="create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value ="update_time",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
