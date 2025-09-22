package com.group.character.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 百炼AI服务配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai.bailian")
public class BailianConfig {
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 服务端点
     */
    private String endpoint = "https://dashscope.aliyuncs.com";
    
    /**
     * 向量模型配置
     */
    private EmbeddingConfig embedding = new EmbeddingConfig();
    
    /**
     * 对话模型配置
     */
    private ChatConfig chat = new ChatConfig();
    
    /**
     * 模型广场配置
     */
    private ModelGalleryConfig modelGallery = new ModelGalleryConfig();
    
    /**
     * 初始化配置
     */
    public void init() {
        // 初始化配置，后续在服务中使用
    }
    
    @Data
    public static class EmbeddingConfig {
        /**
         * 向量模型名称
         */
        private String model = "text-embedding-v1";
        
        /**
         * 最大token数
         */
        private Integer maxTokens = 2048;

        /**
         * 分块大小（字符数）
         */
        private Integer chunkSize = 500;

        /**
         * 分块重叠（字符数）
         */
        private Integer chunkOverlap = 50;

        /**
         * 是否启用PCA降维
         */
        private Boolean usePca = false;

        /**
         * PCA目标维度
         */
        private Integer pcaDim = 256;
    }
    
    @Data
    public static class ChatConfig {
        /**
         * 对话模型名称
         */
        private String model = "qwen-turbo";
        
        /**
         * 温度参数
         */
        private Double temperature = 0.7;
        
        /**
         * 最大token数
         */
        private Integer maxTokens = 1000;
        
        /**
         * Top-p参数
         */
        private Double topP = 0.8;
    }
    
    @Data
    public static class ModelGalleryConfig {
        /**
         * 通义千问系列
         */
        private String qwenTurbo = "qwen-turbo";
        private String qwenPlus = "qwen-plus";
        private String qwenMax = "qwen-max";
        
        /**
         * 向量模型
         */
        private String textEmbeddingV1 = "text-embedding-v1";
        
        /**
         * 其他模型
         */
        private String chatglm36b = "chatglm3-6b";
        private String baichuan27b = "baichuan2-7b";
    }
}


