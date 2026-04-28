package com.example.xcx1.controller;

import com.example.xcx1.dto.LoginRequest;
import com.example.xcx1.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录控制器
 * 提供用户认证和JWT令牌生成功能
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * 构造函数注入依赖
     *
     * @param authenticationManager Spring Security认证管理器
     * @param jwtUtil JWT工具类
     */
    public LoginController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }
    /**
     * 用户登录接口
     * 验证用户凭据并生成JWT访问令牌
     *
     * @param req 登录请求对象，包含用户名和密码信息
     * @return 包含JWT令牌的Map对象，格式为 {"token": "jwt_token_string"}
     * @throws org.springframework.security.core.AuthenticationException 当认证失败时抛出异常（用户名或密码错误）
     */
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest req) {
        // 使用认证管理器验证用户凭据
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        // 基于认证成功的信息生成JWT令牌
        String token = jwtUtil.generateToken(auth);
        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        return result;
    }
}
