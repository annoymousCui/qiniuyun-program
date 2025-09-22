package com.group.chat.service.ai;

import com.group.common.entity.Character;
import com.group.common.entity.ChatMessage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage as OpenAIMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * LLM服务 - 只使用基础的LLM模型调用能力
 */
@Service
public class OpenAIService {
    
    @Value("${openai.api-key:your-api-key}")
    private String apiKey;
    
    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;
    
    private OpenAiService openAiService;
    
    private OpenAiService getOpenAiService() {
        if (openAiService == null) {
            openAiService = new OpenAiService(apiKey, Duration.ofSeconds(60));
        }
        return openAiService;
    }
    
    /**
     * 生成AI回复 - 纯LLM模型调用
     */
    public String generateResponse(Character character, List<ChatMessage> history, String userMessage) {
        List<OpenAIMessage> messages = new ArrayList<>();
        
        // 设置角色系统提示
        String systemPrompt = buildSystemPrompt(character);
        messages.add(new OpenAIMessage("system", systemPrompt));
        
        // 添加历史对话（限制最近5条）
        List<ChatMessage> recentHistory = history.size() > 5 ? 
            history.subList(history.size() - 5, history.size()) : history;
        
        for (ChatMessage msg : recentHistory) {
            String role = "user".equals(msg.getRole()) ? "user" : "assistant";
            messages.add(new OpenAIMessage(role, msg.getContent()));
        }
        
        // 添加当前用户消息
        messages.add(new OpenAIMessage("user", userMessage));
        
        // 构建请求 - 只使用基础LLM能力
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(300)
                .temperature(0.7)
                .build();
        
        // 调用LLM API
        try {
            return getOpenAiService().createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();
        } catch (Exception e) {
            // 如果API调用失败，返回简单的角色回复
            return generateFallbackResponse(character, userMessage);
        }
    }
    
    /**
     * 构建系统提示
     */
    private String buildSystemPrompt(Character character) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你正在扮演").append(character.getName()).append("。\n");
        prompt.append("角色描述：").append(character.getDescription()).append("\n");
        if (character.getPersonality() != null) {
            prompt.append("性格特点：").append(character.getPersonality()).append("\n");
        }
        if (character.getBackground() != null) {
            prompt.append("背景故事：").append(character.getBackground()).append("\n");
        }
        prompt.append("请严格按照角色的性格和背景来回复用户，保持角色的一致性。");
        return prompt.toString();
    }
    
    /**
     * 生成备用回复（当LLM调用失败时）
     */
    private String generateFallbackResponse(Character character, String userMessage) {
        // 简单的关键词匹配回复
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("你好") || lowerMessage.contains("hello")) {
            return "你好！我是" + character.getName() + "，很高兴见到你！";
        } else if (lowerMessage.contains("谢谢") || lowerMessage.contains("thank")) {
            return "不客气！" + character.getName() + "很乐意帮助你。";
        } else if (lowerMessage.contains("再见") || lowerMessage.contains("bye")) {
            return "再见！希望我们还能再次交流。";
        } else {
            return "我是" + character.getName() + "，很高兴和你聊天！" + character.getDescription();
        }
    }
    
}
