package bill.framework.web.log;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Value("${spring.application.name:unknown-app}")
    private String appName;


    @Autowired(required = false)
    private LogConsumer logConsumer;

    @SuppressWarnings("NullableProblems")
    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        long startMillis = System.currentTimeMillis();
        LocalDateTime startTime = LocalDateTime.now();
        String path = request.getRequestURI();
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = IdUtil.fastSimpleUUID();
        }
        MDC.put("traceId", traceId);
        try {
            // 先检查是否排除
            if (logConsumer == null || logConsumer.excludePaths().contains(path)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 包装 Request，必须在第一次进入 Filter 时就包裹
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

            filterChain.doFilter(wrappedRequest, wrappedResponse);
            long duration = System.currentTimeMillis() - startMillis;
            LocalDateTime endTime = LocalDateTime.now();

            String ip = request.getRemoteAddr();
            String host = request.getServerName();
            String method = request.getMethod();

            Map<String, String> header = getAllHeaders(request);
            String headerStr = JSONUtil.toJsonStr(header);

            // ========== 2. 获取表单参数 ==========
            String formParams = JSONUtil.toJsonStr(request.getParameterMap());

            // ========== 3. 获取请求体（JSON 等） ==========
            String body = null;
            byte[] bodyBytes = wrappedRequest.getContentAsByteArray();
            if (bodyBytes.length > 0) {
                body = new String(bodyBytes, StandardCharsets.UTF_8);
            }

            // ========== 4. 获取响应体 ==========
            String responseJson = new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);

            RequestLogInfo requestLog = RequestLogInfo.builder()
                    .appName(appName)
                    .ip(ip)
                    .host(host)
                    .httpMethod(method)
                    .path(path)
                    .header(headerStr)
                    .formParams(formParams)
                    .body(body)
                    .response(responseJson)
                    .startTime(startTime)
                    .endTime(endTime)
                    .durationMs(duration)
                    .traceId(traceId)
                    .build();
            if (StpUtil.isLogin()) {
                requestLog.setUserId((String) StpUtil.getLoginId());
            }
            // 异步消费
            logConsumer.requestLog(requestLog);
            wrappedResponse.copyBodyToResponse();
        } finally {
            MDC.remove("traceId");
        }
    }

    /**
     * 获取全部请求头（Header）
     *
     * @param request HttpServletRequest
     * @return Map<String, String> 全部 header
     */
    public  Map<String, String> getAllHeaders(HttpServletRequest request) {
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
}