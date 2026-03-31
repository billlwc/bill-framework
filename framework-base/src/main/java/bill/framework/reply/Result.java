package bill.framework.reply;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.MDC;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result implements Serializable {
    private String code;
    private String msg;
    private Object data;
    private String traceId;

    public Result(String code, String msg) {
        this.code = code;
        this.msg = msg;
        this.traceId = MDC.get("traceId");
    }

    public Result(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.traceId = MDC.get("traceId");
    }

}
