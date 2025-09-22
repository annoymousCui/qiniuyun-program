package com.group.common.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 聊天消息实体
 */
@Data
public class ChatMessage {
    
    private String id;
    private Long userId;
    private String characterId;
    private String content;
    private String messageType; // text, voice, image
    private String voiceUrl; // 语音文件URL
    private Integer duration; // 语音时长(秒)
    private String role; // user, assistant
    private LocalDateTime createTime;
    
}



