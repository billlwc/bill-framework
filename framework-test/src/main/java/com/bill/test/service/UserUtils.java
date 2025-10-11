package com.bill.test.service;

import cn.dev33.satoken.stp.StpUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.math.BigInteger;

@NoArgsConstructor
public class UserUtils {

    /**
     * 获取当前请求对象
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            return servletAttrs.getRequest();
        }
        return null;
    }

    /**
     * 获取当前响应对象
     */
    public static HttpServletResponse getResponse() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            return servletAttrs.getResponse();
        }
        return null;
    }

    public static void login(BigInteger id,Object vo) {
        StpUtil.login(id);
        if(vo!=null){
            setSession(vo);
        }
        getResponse().addHeader("Access-Control-Expose-Headers", StpUtil.getTokenName());
        getResponse().addHeader(StpUtil.getTokenName(), StpUtil.getTokenValue());
    }

    public static BigInteger userId() {
        return (BigInteger) StpUtil.getLoginId();
    }

    public static void setSession(Object vo) {
        setSession("session",vo);
    }

    public static void setSession(String name,Object vo) {
        StpUtil.getSession().set(name, vo);
    }

    public static Object getSession() {
        return getSession("session");
    }

    public static Object getSession(String name) {
        return StpUtil.getSession().get(name);
    }

    public static void logout() {
        StpUtil.logout();
    }
}
