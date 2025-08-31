package com.Vizzini.agent.app;

import com.Vizzini.agent.advisor.MyLoggerAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class PlayWhatApp {

    private static final Logger logger = LoggerFactory.getLogger(PlayWhatApp.class);


    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT =
            "扮演专业全国旅行路线定制专家。开场向用户表明身份，说明可帮助规划个性化深度旅行路线。" +
            "围绕文化探索、自然风光、美食体验、亲子休闲等主题提供方案：" +
            "文化探索类推荐历史名城与非遗路线；自然风光类设计山川湖海生态行程；" +
            "美食体验类安排地域特色餐饮打卡；亲子休闲类规划安全有趣的亲子活动。" +
            "引导用户说明出发地、天数、预算、偏好及同行人员，以便生成高匹配度旅行计划。";

    /**
     * 初始化AI客户端
     * @param dashscopeChatModel
     */
    public PlayWhatApp(ChatModel dashscopeChatModel) {
        // 基于文件的对话记忆的初始化
//        String fileDir = System.getProperty("user.dir") + "chat-memory";
//        ChatMemory chatMemory = new InMemoryChatMemory(fileDir);
//        chatClient = ChatClient.builder(dashscopeChatModel)
//                .defaultSystem(SYSTEM_PROMPT)
//                .defaultAdvisors(
//                        new MessageChatMemoryAdvisor(chatMemory)
//                )
//                .build();

        // 普通初始化
        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new MyLoggerAdvisor()
                )
                .build();
    }

    /**
     * AI基础对话
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        logger.info("content: {}", content);
        return content;
    }

    /**
     * 创建一个旅行路线报告类
     * @param title
     * @param suggestions
     */
    // 使用 Java14 引入的 record 特性快速定义
    // TravelReport 类定义了数据结构，真正的 JSON 处理是由 SpringAI 框架在后台自动生成的
    record TravelReport(String title, List<String> suggestions) {
    }


    /**
     *
     * @param message
     * @param chatId
     * @return
     */
    public TravelReport doChatWithReport(String message, String chatId) {
        TravelReport travelReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成旅行路线结果，标题为{用户名}的旅行路线建议，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        // 对话记忆的上限
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                // 当调用 TravelReport.class 时
                // SpringAI 会让 AI 模型按照 TravelReport 的结构生成 JSON 格式响应
                // 并自动将该 JSON 响应反序列化为 TravelReport 对象
                .entity(TravelReport.class);
        log.info("travelReport: {}", travelReport);
        return travelReport;
    }


}