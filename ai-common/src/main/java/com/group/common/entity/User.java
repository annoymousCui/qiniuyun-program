package com.group.common.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
public class User {
    
    private Long id;
    private String username;
    private String email;
    private String password;
    private String nickname;
    private String avatar;
    private Integer status; // 0-禁用 1-启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
}
