package bill.framework.web.exception;


import bill.framework.enums.ResponseCode;
import bill.framework.exception.BusinessException;
import bill.framework.message.MessageSourceService;
import bill.framework.reply.Result;
import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class BaseExceptionHandler {

    private final MessageSourceService sourceService;

    /**
     * 获取国际化消息
     */
    private String getMessage(String messageKey, Object[] args) {
        return sourceService.getMessage(messageKey, args);
    }

    /**
     * 自定义业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("【业务异常】path={}, params={}, error={}",
                request.getRequestURI(),
                JSONUtil.toJsonStr(request.getParameterMap()),
                e.getMessage());

        String message = getMessage(e.getMessage(), e.getArgs());
        return ResponseEntity.status(e.getHttpStatus()).body(new Result(e.getCode(), message));
    }

    /**
     * 参数校验异常（JSR303 @Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMsg = (fieldError != null) ? fieldError.getDefaultMessage() : "Validation error";

        log.error("【参数校验异常】path={}, params={}, error={}",
                request.getRequestURI(),
                JSONUtil.toJsonStr(request.getParameterMap()),
                errorMsg);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new Result(ResponseCode.BAD_REQUEST.getCode(),
                        getMessage(ResponseCode.BAD_REQUEST.getMsg(), new Object[]{errorMsg})));
    }

    /**
     * 404 - 资源不存在
     */
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<Result> handleNotFoundException(HttpServletRequest request) {
        log.warn("【资源不存在】path={}, params={}",
                request.getRequestURI(),
                JSONUtil.toJsonStr(request.getParameterMap()));

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new Result(ResponseCode.NOT_FOUND.getCode(),
                        getMessage(ResponseCode.NOT_FOUND.getMsg(), new Object[]{request.getRequestURI()})));
    }

    /**
     * 登录失效或未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<Result> handleNotLoginException(NotLoginException e) {
        log.warn("【认证异常】{}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new Result(ResponseCode.UNAUTHORIZED.getCode(),
                        getMessage(ResponseCode.UNAUTHORIZED.getMsg(), null)));
    }

    /**
     * Spring 常见异常，如类型转换、请求方式错误等
     */
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Result> handleSpringCommonException(Exception e, HttpServletRequest request) {
        log.error("【Spring通用异常】path={}, params={}, error={}",
                request.getRequestURI(),
                JSONUtil.toJsonStr(request.getParameterMap()),
                e.getMessage(),e);

        return ResponseEntity
                .status(ResponseCode.BAD_REQUEST.getHttpStatus())
                .body(new Result(ResponseCode.BAD_REQUEST.getCode(),
                        getMessage(ResponseCode.BAD_REQUEST.getMsg(), new Object[]{e.getMessage()})));
    }

    /**
     * 兜底异常处理（防止漏网之鱼）
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Result> handleThrowable(Throwable e, HttpServletRequest request) {
        log.error("【系统异常】path={}, params={}",
                request.getRequestURI(),
                JSONUtil.toJsonStr(request.getParameterMap()),
                e);

        return ResponseEntity
                .status(ResponseCode.ERROR_SYSTEM.getHttpStatus())
                .body(new Result(ResponseCode.ERROR_SYSTEM.getCode(),
                        getMessage(ResponseCode.ERROR_SYSTEM.getMsg(), null)));
    }
}