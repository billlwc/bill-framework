package com.bill.test.controller;

import bill.framework.enums.ResponseCode;
import bill.framework.enums.SysResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MyResponseCode implements ResponseCode {

   MY_ERROR("BUSINESS_ERROR", "我的异常", SysResponseCode.BUSINESS_ERROR.getHttpStatus());

    private  String code = "";
    private String msg = "";
    private int httpStatus = 0;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public int getHttpStatus() {
        return this.httpStatus;
    }
}
