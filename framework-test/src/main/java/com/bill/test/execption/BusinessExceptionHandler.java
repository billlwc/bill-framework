package com.bill.test.execption;

import bill.framework.enums.SysResponseCode;
import bill.framework.reply.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@Order(1) // 优先级比父类高（父类可不加或加 @Order(10)）
public class BusinessExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result> handleBizException(BizException e) {
        log.warn("【业务异常】{}", e.getMessage());
        return ResponseEntity
                .status(SysResponseCode.NOT_FOUND.getHttpStatus())
                .body(new Result(SysResponseCode.SYSTEM_ERROR.getCode(), e.getMessage()));
    }

}
