package com.bill.test;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import java.net.InetAddress;


@Slf4j
@SpringBootApplication
public class TestApplication {

    @SneakyThrows
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(TestApplication.class, args);
        Environment env = application.getEnvironment();
        // 获取本机IP和端口
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String appName = env.getProperty("spring.application.name", "Application");
        String activeProfile = String.join(",", env.getActiveProfiles().length > 0 ? env.getActiveProfiles() : new String[]{"default"});
        // 构建基础URL
        String baseUrl = String.format("http://%s:%s%s", ip, port, contextPath);
        // ANSI 颜色
        String sb = "\n" +
                "----------------------------------------------------------\n" +
                String.format("%s  Application: %s%s%n", "\033[34m", appName, "\033[0m") +
                String.format("%s  Profile:     %s%s%n", "\033[36m", activeProfile, "\033[0m") +
                String.format("%s  Local:       %shttp://localhost:%s%s/%s%n", "\033[32m", "\033[36m", port, contextPath, "\033[0m") +
                String.format("%s  External:    %s%s/%s%n", "\033[32m", "\033[36m", baseUrl, "\033[0m") +
                String.format("%s  Swagger UI:  %s%s/swagger-ui.html%s%n", "\033[32m", "\033[36m", baseUrl, "\033[0m") +
                String.format("%s  Doc:         %s%s/doc.html%s%n", "\033[32m", "\033[36m", baseUrl, "\033[0m") +
                "----------------------------------------------------------";
        log.info(sb);
    }

}
