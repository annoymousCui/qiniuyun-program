package com.group.character;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 角色服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CharacterServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CharacterServiceApplication.class, args);
    }
    
}
