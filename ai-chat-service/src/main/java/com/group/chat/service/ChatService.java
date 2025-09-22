package com.group.chat.service;

import com.group.common.entity.ChatMessage;
import com.group.common.entity.Character;
import com.group.chat.repository.ChatMessageRepository;
import com.group.chat.service.ai.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 聊天服务
 */
@Service
public class ChatService {
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private LLMService llmService;
    
    /**
     * 发送消息
     */
    public ChatMessage sendMessage(Long userId, String characterId, String content) {
        // 保存用户消息
        ChatMessage userMessage = new ChatMessage();
        userMessage.setId(UUID.randomUUID().toString());
        userMessage.setUserId(userId);
        userMessage.setCharacterId(characterId);
        userMessage.setContent(content);
        userMessage.setMessageType("text");
        userMessage.setRole("user");
        userMessage.setCreateTime(LocalDateTime.now());
        chatMessageRepository.save(userMessage);
        
        // 获取角色信息
        Character character = mongoTemplate.findById(characterId, Character.class);
        if (character == null) {
            throw new RuntimeException("角色不存在");
        }
        
        // 获取聊天历史
        List<ChatMessage> history = getChatHistory(userId, characterId, 0, 10);
        
        // 调用LLM生成回复 - 只使用基础LLM能力
        String aiResponse = llmService.generateCharacterResponse(character, history, content);
        
        // 保存AI回复
        ChatMessage aiMessage = new ChatMessage();
        aiMessage.setId(UUID.randomUUID().toString());
        aiMessage.setUserId(userId);
        aiMessage.setCharacterId(characterId);
        aiMessage.setContent(aiResponse);
        aiMessage.setMessageType("text");
        aiMessage.setRole("assistant");
        aiMessage.setCreateTime(LocalDateTime.now());
        chatMessageRepository.save(aiMessage);
        
        return aiMessage;
    }
    
    /**
     * 获取聊天历史
     */
    public List<ChatMessage> getChatHistory(Long userId, String characterId, int page, int size) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("characterId").is(characterId));
        query.with(PageRequest.of(page, size));
        query.with(org.springframework.data.domain.Sort.by("createTime").ascending());
        
        return mongoTemplate.find(query, ChatMessage.class);
    }
    
    /**
     * 清空聊天记录
     */
    public void clearChatHistory(Long userId, String characterId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("characterId").is(characterId));
        mongoTemplate.remove(query, ChatMessage.class);
    }
    
}
