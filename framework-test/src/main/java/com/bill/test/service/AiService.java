package com.bill.test.service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ChatClient chatClient;

    public AiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * 根据用户输入的问题，调用 AI 模型并返回结果
     * @param question 用户提问
     * @return 模型回答
     */
    public String askQuestion(String question) {
        return chatClient.prompt(question)
                .call()
                .content();
    }

    /**
     * 使用结构化输出：返回一个 Java 对象
     */
    public Poem generatePoem(String theme) {
        // 定义请求 prompt 模板
        String prompt = String.format("Write a poem about %s, clearly indicate title and content.", theme);
        return chatClient.prompt(prompt)
                .call()
                .entity(Poem.class);
    }

    public static record Poem(String title, String content) {}
}
