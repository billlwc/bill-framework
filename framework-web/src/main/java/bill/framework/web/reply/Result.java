package bill.framework.web.reply;


import bill.framework.web.enums.ResponseStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result implements Serializable {
    private String code;
    private String message;
    private Object data;
    private String uuid;

    public Result(Object data){
        this.code = ResponseStatusEnum.SUCCESS.getCode();
        this.message = ResponseStatusEnum.SUCCESS.getMessage();
        this.data = data;
    }

    public Result(String code,String message){
        this.code = code;
        this.message = message;
    }

    public Result(String code,String message,String uuid){
        this.code = code;
        this.message = message;
        this.uuid = uuid;
    }


}
