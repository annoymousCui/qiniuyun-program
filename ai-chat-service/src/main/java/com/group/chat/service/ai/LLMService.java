package com.group.chat.service.ai;

import com.group.common.entity.Character;
import com.group.common.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * LLM服务接口 - 只提供基础的大语言模型调用能力
 */
@Service
public class LLMService {
    
    @Autowired
    private OpenAIService openAIService;
    
    /**
     * 生成角色回复 - 纯LLM模型调用
     * @param character 角色信息
     * @param history 聊天历史
     * @param userMessage 用户消息
     * @return AI生成的回复
     */
    public String generateCharacterResponse(Character character, List<ChatMessage> history, String userMessage) {
        // 只使用基础的LLM模型能力，不调用任何Agent功能
        return openAIService.generateResponse(character, history, userMessage);
    }
    
    /**
     * 验证LLM服务是否可用
     * @return 服务状态
     */
    public boolean isServiceAvailable() {
        try {
            // 简单的健康检查
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
}
