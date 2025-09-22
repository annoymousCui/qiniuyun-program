package com.group.user.controller;

import com.group.common.entity.User;
import com.group.common.result.Result;
import com.group.common.util.JwtUtil;
import com.group.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody User user) {
        User registeredUser = userService.register(user);
        String token = jwtUtil.generateToken(registeredUser.getId(), registeredUser.getUsername());
        
        Map<String, Object> data = new HashMap<>();
        data.put("user", registeredUser);
        data.put("token", token);
        
        return Result.success("注册成功", data);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestParam String username, 
                                           @RequestParam String password) {
        User user = userService.login(username, password);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("token", token);
        
        return Result.success("登录成功", data);
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userService.getUserByUsername(username);
        return Result.success(user);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<User> updateUser(@RequestHeader("Authorization") String token,
                                 @RequestBody User user) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User updatedUser = userService.updateUser(username, user);
        return Result.success("更新成功", updatedUser);
    }
    
}



