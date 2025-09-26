package bill.framework.web.advice;


import bill.framework.message.MessageSourceService;
import bill.framework.reply.Result;
import bill.framework.web.annotation.NoResult;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ResultControllerAdvice implements ResponseBodyAdvice<Object> {

    private final MessageSourceService messageService;



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

        if(data instanceof String str){
            // 这里假设 data 是国际化 key
            String message = messageService.getMessage(str, null);
            return new Result(message);
        }

        return new Result(data);

    }
}

