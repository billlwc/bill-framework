package bill.framework.web.exception;

import bill.framework.web.enums.ResponseCode;
import bill.framework.web.reply.Result;
import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
//@ConditionalOnProperty(name = "base", havingValue = "true")
public class BaseExceptionHandler {

    /**
     * 处理自定义业务异常 BusinessException
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e, HttpServletRequest request, HttpServletResponse response) {
        // 根据异常状态码设置 HTTP 响应状态
        int status = switch (e.getHttpStatus()) {
            case 401 -> HttpStatus.UNAUTHORIZED.value();
            case 403 -> HttpStatus.FORBIDDEN.value();
            default -> HttpStatus.BAD_REQUEST.value();
        };
        response.setStatus(status);
        String path = request.getRequestURI();
        String params = JSONUtil.toJsonStr(request.getParameterMap());
        log.error("{}  path={}, params={}",e.getMessage(), path, params);
        return new Result(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        String params = JSONUtil.toJsonStr(request.getParameterMap());
        log.error("参数错误  path={}, params={}", path, params);
        return new Result(ResponseCode.BAD_REQUEST.getCode(), e.getMessage());
    }

    /**
     * 404
     */
    @org.springframework.web.bind.annotation.ExceptionHandler({ClassCastException.class,NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result handleClassCastException(Throwable e, HttpServletRequest request) {
        return new Result(ResponseCode.NOT_FOUND.getCode(), request.getRequestURI()+" 404");
    }


    /**
     * 处理登陆失败
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result handleThrowable(NotLoginException e) {
        return new Result(ResponseCode.UNAUTHORIZED.getCode(), e.getMessage());
    }


    /**
     * 处理所有未捕获的异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleThrowable(Throwable e, HttpServletRequest request) {

        String path = request.getRequestURI();
        String params = JSONUtil.toJsonStr(request.getParameterMap());
        log.error("系统异常  path={}, params={}", path, params, e);
        return new Result(ResponseCode.ERROR_SYSTEM.getCode(), "系统错误");
    }


}
