package com.Vizzini.agent.advisor;

import org.springframework.ai.chat.client.advisor.api.*;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import reactor.core.publisher.Flux;

import java.util.*;

public class SafeGuardAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
    // 敏感词库
    private final Set<String> sensitiveWords = new HashSet<>(Arrays.asList(
            "请问", "崇拜", "666" // 示例敏感词，可根据实际需求扩展
    ));
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        try {
            // 检查请求内容是否包含敏感词，通过调用自定义方法 containsSensitiveWords() 来实现
            if (containsSensitiveWords(advisedRequest.userText())) {
                // return new AdvisedResponse("请求包含敏感信息，已被拦截", null);
                return createErrorResponse("请求包含敏感信息，已被拦截。");
            }
            // 否则继续执行后续处理逻辑
            return chain.nextAroundCall(advisedRequest);
        } catch (Exception e) {
            // 处理检查过程中的异常（如敏感词库加载失败等）
            return createErrorResponse("安全检测服务异常。");
            //return new AdvisedResponse("安全检测服务异常", e);
        }
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        try {
            // 检查请求内容是否包含敏感词，通过调用自定义方法 containsSensitiveWords() 来实现
            if (containsSensitiveWords(advisedRequest.userText())) {
                // 若是包含敏感词，返回错误信息的Flux
                // return Flux.just(new AdvisedResponse("请求包含敏感信息，已被拦截。", null));
                return Flux.just(createErrorResponse("请求包含敏感信息，已被拦截。"));
            }
            // 否则继续执行后续处理逻辑
            return chain.nextAroundStream(advisedRequest);
        } catch (Exception e) {
            return Flux.just(createErrorResponse("安全检测服务异常"));
            //return new AdvisedResponse("安全检测服务异常", e);
        }
    }

    @Override
    public String getName() {
        return "SafeGuardAdvisor";
    }

    @Override
    public int getOrder() {
        // 优先级，数字越小优先级越高
        return 0;
    }

    /**
     * 检查用户输入的文本是否包含敏感词
     * @param text 用户请求文本
     * @return 是否包含敏感词
     */
    private boolean containsSensitiveWords(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        // 统一转为小写进行比较（避免大小写差异）
        String normalizedText = text.toLowerCase();

        for (String word : sensitiveWords) {
            // 使用indexOf避免重复创建子字符串
            if (normalizedText.indexOf(word.toLowerCase()) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建错误响应
     * @param message 错误消息
     * @return 包含错误信息的AdvisedResponse
     */
    private AdvisedResponse createErrorResponse(String message) {
        // 创建AssistantMessage而不是SystemMessage
        AssistantMessage assistantMessage = new AssistantMessage(message);

        // 创建Generation
        Generation generation = new Generation(assistantMessage);

        // 创建包含错误信息的元数据
        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("error", true);
        metadataMap.put("errorMessage", message);
        metadataMap.put("errorCode", "SECURITY_BLOCK");

        // 创建ChatResponse
//        ChatResponse errorResponse = new ChatResponse() {
//            @Override
//            public List<Generation> getResults() {
//                return List.of(generation);
//            }
//
//            @Override
//            public ChatResponseMetadata getMetadata() {
//                return new ChatResponseMetadata() {
//                    public Map<String, Object> getMetadata() {
//                        return metadataMap;
//                    }
//
//                    @Override
//                    public String toString() {
//                        return "ChatResponseMetadata{" + getMetadata() + "}";
//                    }
//                };
//            }
//        };

        ChatResponseMetadata metadata = new ChatResponseMetadata() {
            private final Map<String, Object> metadataMap = Map.of(
                    "error", true,
                    "errorMessage", message,
                    "errorCode", "SECURITY_BLOCK"
            );
        };
        ChatResponse errorResponse = new ChatResponse(List.of(generation), metadata);


        return new AdvisedResponse(errorResponse, Collections.emptyMap());
    }



}
