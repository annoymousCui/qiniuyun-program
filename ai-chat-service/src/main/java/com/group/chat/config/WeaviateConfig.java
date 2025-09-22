package com.group.chat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Weaviate向量数据库配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai.weaviate")
public class WeaviateConfig {
    
    /**
     * Weaviate服务地址
     */
    private String url = "http://127.0.0.1:8080";
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 请求超时时间（秒）
     */
    private Integer timeout = 30;
    
    /**
     * 向量化器配置
     */
    private VectorizerConfig vectorizer = new VectorizerConfig();
    
    /**
     * 集合配置
     */
    private CollectionsConfig collections = new CollectionsConfig();
    
    @Data
    public static class VectorizerConfig {
        /**
         * 向量模型
         */
        private String model = "text-embedding-v1";
        
        /**
         * 向量维度
         */
        private Integer dimensions = 1536;
    }
    
    @Data
    public static class CollectionsConfig {
        /**
         * 文档集合配置
         */
        private DocumentsConfig documents = new DocumentsConfig();
    }
    
    @Data
    public static class DocumentsConfig {
        /**
         * 集合名称
         */
        private String name = "Documents";
        
        /**
         * 集合描述
         */
        private String description = "文档向量存储集合";
        
        /**
         * 属性配置
         */
        private List<PropertyConfig> properties;
    }
    
    @Data
    public static class PropertyConfig {
        /**
         * 属性名称
         */
        private String name;
        
        /**
         * 数据类型
         */
        private String dataType;
        
        /**
         * 属性描述
         */
        private String description;
    }
}


