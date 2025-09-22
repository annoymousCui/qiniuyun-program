package com.group.voice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 语音服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class VoiceServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(VoiceServiceApplication.class, args);
    }
    
}
