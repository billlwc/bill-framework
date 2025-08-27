package bill.framework.web.version;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @ClassName ApiConfiguration
 * @Description
 * @Author c
 * @Date 2021/9/11 8:16 下午
 * @Version V1.0
 **/
@Configuration
public class ApiVersionConfiguration implements WebMvcRegistrations {

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new ApiVersionRequestMappingHandlerMapping();
    }

}
