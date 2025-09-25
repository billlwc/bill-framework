package bill.framework.web.log;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MethodLogInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 应用名称 */
    private String appName;

    /** 用户ID */
    private String userId;

    /** 请求路径 */
    private String path;

    /** 请求参数（JSON 字符串） */
    private String params;

    /** 返回值（JSON 字符串） */
    private String response;

    /** 请求开始时间 */
    private LocalDateTime startTime;

    /** 请求结束时间 */
    private LocalDateTime endTime;

    /** 耗时（毫秒） */
    private long durationMs;

    /** 可选：TraceId 或唯一标识 */
    private String traceId;

    /** 应用名称 */
    private String title;

    /** 应用名称 */
    private String message;
}