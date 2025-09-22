package com.group.chat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * SBERT向量生成服务配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai.sbert")
public class SBertConfig {
    
    /**
     * SBERT服务API地址
     */
    private String apiUrl = "http://localhost:8001";
    
    /**
     * 模型名称
     */
    private String modelName = "all-MiniLM-L6-v2";
    
    /**
     * 请求超时时间（毫秒）
     */
    private Integer timeout = 30000;
}


