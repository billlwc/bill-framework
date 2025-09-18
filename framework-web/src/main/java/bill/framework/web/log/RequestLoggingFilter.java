package bill.framework.web.log;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
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
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


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

            // 包装 response 捕获返回值
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
            filterChain.doFilter(request, wrappedResponse);

            long duration = System.currentTimeMillis() - startMillis;
            LocalDateTime endTime = LocalDateTime.now();

            String ip = request.getRemoteAddr();
            String host = request.getServerName();
            String method = request.getMethod();
            String paramsJson = JSONUtil.toJsonStr(request.getParameterMap());
            String responseJson = new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);

            RequestLogInfo requestLog = RequestLogInfo.builder()
                    .appName(appName)
                    .ip(ip)
                    .host(host)
                    .httpMethod(method)
                    .path(path)
                    .params(paramsJson)
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
}