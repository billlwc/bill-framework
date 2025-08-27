package bill.framework.web.exception;

import bill.framework.web.enums.ResponseStatusEnum;
import bill.framework.web.reply.Result;
import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.UUID;

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
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String path = request.getRequestURI();
        String params = JSONUtil.toJsonStr(request.getParameterMap());
        log.error("业务异常 uuid={}, path={}, params={}", uuid, path, params, e);
        return new Result(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String path = request.getRequestURI();
        String params = JSONUtil.toJsonStr(request.getParameterMap());
        log.error("参数错误 uuid={}, path={}, params={}", uuid, path, params, e);
        return new Result(ResponseStatusEnum.BAD_REQUEST.getCode(), e.getMessage(), uuid);
    }

    /**
     * 404
     */
    @org.springframework.web.bind.annotation.ExceptionHandler({ClassCastException.class,NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result handleClassCastException(Throwable e, HttpServletRequest request) {

        String uuid = UUID.randomUUID().toString().replace("-", "");
        String path = request.getRequestURI();
        String params = JSONUtil.toJsonStr(request.getParameterMap());
        log.error("请求404 uuid={}, path={}, params={}", uuid, path, params, e);
        return new Result(ResponseStatusEnum.NOT_FOUND.getCode(), path+"  404", uuid);
    }


    /**
     * 处理登陆失败
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result handleThrowable(NotLoginException e) {
        return new Result(ResponseStatusEnum.UNAUTHORIZED.getCode(), e.getMessage());
    }


    /**
     * 处理所有未捕获的异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleThrowable(Throwable e, HttpServletRequest request) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String path = request.getRequestURI();
        String params = JSONUtil.toJsonStr(request.getParameterMap());
        log.error("系统异常 uuid={}, path={}, params={}", uuid, path, params, e);
        return new Result(ResponseStatusEnum.ERROR_SYSTEM.getCode(), "系统错误", uuid);
    }


}
