package bill.framework.web.bo;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Schema(name="RequestBO",description="RequestBO")
@ToString
public class RequestBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name="id",description="",nullable=false)
    private Long id;

    @Schema(name="keyWord",description="",nullable=false)
    private String keyWord;

    @Schema(name="startTime",description="",nullable=false)
    private LocalDateTime startTime;

    @Schema(name="endTime",description="",nullable=false)
    private LocalDateTime endTime;
}
