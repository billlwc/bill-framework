package bill.framework.reply;


import bill.framework.enums.SysResponseCode;
import bill.framework.message.MessageSourceService;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.Serializable;

@Data
@Slf4j
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result implements Serializable {
    private String code;
    private String msg;
    private Object data;
    private String traceId;

    public Result(Object data){
        this.code = SysResponseCode.SUCCESS.getCode();
        this.msg = SpringUtil.getBean(MessageSourceService.class).getMessage(SysResponseCode.SUCCESS.getMsg(), null);
        this.data = data;
    }



    public Result(String code,String msg){
        this.code = code;
        this.msg = msg;
        this.traceId= MDC.get("traceId");
    }


}
