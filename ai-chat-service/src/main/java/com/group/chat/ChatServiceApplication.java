package com.group.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 聊天服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ChatServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
    }
    
}



