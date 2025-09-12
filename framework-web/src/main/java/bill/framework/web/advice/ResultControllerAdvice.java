package bill.framework.web.advice;


import bill.framework.reply.Result;
import bill.framework.web.annotation.NoResult;
import cn.hutool.json.JSONUtil;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.AnnotatedElement;

@SuppressWarnings("NullableProblems")
@RestControllerAdvice(basePackages = "com")
public class ResultControllerAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !Result.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object data, MethodParameter returnType, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> converterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        AnnotatedElement element = returnType.getAnnotatedElement();
        NoResult noResult = AnnotationUtils.findAnnotation(element, NoResult.class);
        if(noResult != null || data instanceof Result){
            return data;
        }

        if(data instanceof String){
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return JSONUtil.toJsonStr(new Result(data));
        }

        return new Result(data);
    }
}

