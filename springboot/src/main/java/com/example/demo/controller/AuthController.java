package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器（注册、登录）
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {
        try {
            // 参数验证
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ApiResponse.error(400, "用户名不能为空");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ApiResponse.error(400, "密码不能为空");
            }
            if (request.getPassword().length() < 6) {
                return ApiResponse.error(400, "密码长度不能少于6位");
            }

            String message = userService.register(request);
            return ApiResponse.success(message, null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("注册失败：" + e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<User> login(@RequestBody LoginRequest request) {
        try {
            // 参数验证
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ApiResponse.error(400, "用户名不能为空");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ApiResponse.error(400, "密码不能为空");
            }

            User user = userService.login(request);
            return ApiResponse.success("登录成功", user);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("登录失败：" + e.getMessage());
        }
    }
}












