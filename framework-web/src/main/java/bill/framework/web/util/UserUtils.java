package bill.framework.web.util;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigInteger;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

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

    /**
     * 获取全部请求头（Header）
     *
     * @return Map<String, String> 全部 header
     */
    public static  Map<String, String> getAllHeaders() {
        HttpServletRequest request=getRequest();
        Map<String, String> headers = new LinkedHashMap<>();
        if (request == null) {
            return headers;
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            headers.put(name, value);
        }

        return headers;
    }

    public static String getClientIp() {
        HttpServletRequest request = getRequest(); // 假设你这里封装了 RequestContextHolder
        if (request == null) {
            return "none";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr(); // 如果前面都没拿到，最后取直连 IP
        }

        // 处理多级代理情况，取第一个真实 IP (使用 substring 性能高于 split)
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }

        // 防止最终拿到的是 0:0:0:0:0:0:0:1 (IPv6的本机地址)，统一转成 127.0.0.1
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }


    /**
     * 获取客户端完整信息（包含 IP、User-Agent、语言等）
     */
    public static Map<String, Object> getClientInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        HttpServletRequest request=getRequest();
        if (request == null) {
            info.put("error", "Request is null");
            return info;
        }
        info.put("ip", getClientIp());
        info.put("method", request.getMethod());
        info.put("protocol", request.getProtocol());
        info.put("scheme", request.getScheme());
        info.put("headers", getAllHeaders());
        info.put("contentType", request.getContentType());
        info.put("uri", request.getRequestURI());
        info.put("queryString", request.getQueryString());

        // 其他所有 header 也可收集（可选）
        StringBuilder headers = new StringBuilder();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            headers.append(name).append(": ").append(request.getHeader(name)).append("\n");
        }
        info.put("allHeaders", headers.toString());

        return info;
    }

    public  static String login(Object id,Object vo) {
        StpUtil.login(id);
        if(vo!=null){
            setSession(vo);
        }
        getResponse().addHeader("Access-Control-Expose-Headers", StpUtil.getTokenName());
        getResponse().addHeader(StpUtil.getTokenName(), StpUtil.getTokenValue());
        return StpUtil.getTokenValue();
    }

    public static Object userId() {
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
