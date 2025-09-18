package bill.framework.web.bo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Schema(name="RequestPageBO",description="RequestPageBO")
@ToString(callSuper = true)
public class RequestPageBO extends RequestBO{

    @Schema(name="pageNum",description="页码",nullable=false)
    private Integer pageNum=1;

    @Schema(name="pageSize",description="每页条数",nullable=false)
    private Integer pageSize=10;

    @Schema(name="sortField",description="排序列名",nullable=false)
    private String sortField;

    @Schema(name="sortType",description="ASC/DESC",nullable=false)
    private String sortType;

    //防止分页缓存
    private long timestamp=System.currentTimeMillis()/2000;

    public Page getPage() {
       return new Page<>(getPageNum(),getPageSize());
    }


}
