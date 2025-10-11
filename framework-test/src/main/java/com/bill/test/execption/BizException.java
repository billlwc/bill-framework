package com.bill.test.execption;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class BizException extends RuntimeException {


    /** 业务异常码，对应 ResponseCode.code */
    private String code;

    /** 国际化消息占位符参数 */
    private Object[] args;

    /** HTTP 状态码 */
    private int httpStatus;

    /** 构造函数：自定义 code、messageKey、httpStatus、占位符参数 */
    public BizException(String code, String messageKey, int httpStatus, Object[] args) {
        super(messageKey);
        this.code = code;
        this.httpStatus = httpStatus;
        this.args = args;
    }


    }
