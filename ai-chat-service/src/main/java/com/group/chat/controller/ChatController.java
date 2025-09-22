package com.group.chat.controller;

import com.group.common.entity.ChatMessage;
import com.group.common.result.Result;
import com.group.common.util.JwtUtil;
import com.group.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天控制器
 */
@RestController
@RequestMapping("/chat")
@CrossOrigin
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 发送消息
     */
    @PostMapping("/send")
    public Result<ChatMessage> sendMessage(@RequestHeader("Authorization") String token,
                                         @RequestParam String characterId,
                                         @RequestParam String content) {
        Long userId = jwtUtil.extractUserId(token.substring(7));
        ChatMessage message = chatService.sendMessage(userId, characterId, content);
        return Result.success("发送成功", message);
    }
    
    /**
     * 获取聊天历史
     */
    @GetMapping("/history")
    public Result<List<ChatMessage>> getChatHistory(@RequestHeader("Authorization") String token,
                                                   @RequestParam String characterId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "50") int size) {
        Long userId = jwtUtil.extractUserId(token.substring(7));
        List<ChatMessage> messages = chatService.getChatHistory(userId, characterId, page, size);
        return Result.success(messages);
    }
    
    /**
     * 清空聊天记录
     */
    @DeleteMapping("/clear")
    public Result<Void> clearChatHistory(@RequestHeader("Authorization") String token,
                                       @RequestParam String characterId) {
        Long userId = jwtUtil.extractUserId(token.substring(7));
        chatService.clearChatHistory(userId, characterId);
        return Result.success("清空成功");
    }
    
}



