package bill.framework.web.log;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


@Component
@Slf4j
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Value("${spring.application.name:unknown-app}")
    private String appName;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired(required = false)
    private RequestLogConsumer requestLogConsumer;

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
            if (requestLogConsumer == null || requestLogConsumer.excludePaths().contains(path)) {
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
            String paramsJson = objectMapper.writeValueAsString(request.getParameterMap());
            String responseJson = new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);

            RequestLog requestLog = RequestLog.builder()
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

            // 异步消费
            Thread.ofVirtual().start(() -> requestLogConsumer.consume(requestLog));

            wrappedResponse.copyBodyToResponse();
        } finally {
            MDC.remove("traceId");
        }
    }
}