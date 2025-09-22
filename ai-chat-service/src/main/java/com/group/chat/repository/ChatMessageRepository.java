package com.group.chat.repository;

import com.group.common.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 聊天消息数据访问层
 */
@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    
    List<ChatMessage> findByUserIdAndCharacterIdOrderByCreateTimeAsc(Long userId, String characterId);
    
    void deleteByUserIdAndCharacterId(Long userId, String characterId);
    
}
