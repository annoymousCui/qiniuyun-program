package com.group.common.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色实体
 */
@Data
public class Character {
    
    private String id;
    private String name;
    private String description;
    private String personality;
    private String background;
    private String avatar;
    private List<String> tags;
    private String category; // 分类：文学、历史、科幻等
    private Integer popularity; // 热度
    private Integer status; // 0-禁用 1-启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
}



