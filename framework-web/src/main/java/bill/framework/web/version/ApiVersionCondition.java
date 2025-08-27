package bill.framework.web.version;

import bill.framework.web.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

@SuppressWarnings("NullableProblems")
@Slf4j
public record ApiVersionCondition(int apiVersion) implements RequestCondition<ApiVersionCondition> {

    public static final String HEADER_VERSION = "Version";
    private static final int DEFAULT_VERSION = 1;

    @Override
    public ApiVersionCondition combine(ApiVersionCondition other) {
        // 方法注解优先于类注解
        return new ApiVersionCondition(other.apiVersion());
    }

    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        String v = request.getHeader(HEADER_VERSION);
        int version = DEFAULT_VERSION;
        if (StringUtils.hasText(v)) {
            try {
                version = Integer.parseInt(v);
            } catch (NumberFormatException e) {
                log.error("非法版本号: {}", v);
                throw new BusinessException(HEADER_VERSION+" is error:"+v);
            }
        }
        // 请求的版本号 >= 配置的版本号时，匹配成功
        return version >= this.apiVersion ? this : null;
    }

    @Override
    public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
        // 优先选择最新的版本
        return other.apiVersion - this.apiVersion;
    }
}
