package bill.framework.web.advice;

import bill.framework.enums.SysResponseCode;
import bill.framework.web.message.MessageSourceService;
import bill.framework.reply.Result;
import bill.framework.web.annotation.NoResult;
import bill.framework.web.util.JSONUtil;
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
//@RestControllerAdvice(basePackages = "com")
@RestControllerAdvice
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

        String successCode = SysResponseCode.SUCCESS.getCode();
        String successMsg = messageService.getMessage(SysResponseCode.SUCCESS.getMsg(), null);

        if(data instanceof String str){
            String message = messageService.getMessage(str, null);
            return JSONUtil.toJson(new Result(successCode, successMsg, message));
        }

        return new Result(successCode, successMsg, data);

    }
}

