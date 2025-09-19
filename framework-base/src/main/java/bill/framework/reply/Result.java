package bill.framework.reply;


import bill.framework.enums.ResponseCode;
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
        this.code = ResponseCode.SUCCESS.getCode();
        this.msg = ResponseCode.SUCCESS.getMsg();
        this.data = data;
    }

    public Result(String code,String msg){
        this.code = code;
        this.msg = msg;
        this.traceId= MDC.get("traceId");
    }


}
