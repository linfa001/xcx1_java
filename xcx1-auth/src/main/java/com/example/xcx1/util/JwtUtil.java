package com.example.xcx1.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT工具类
 * 提供JWT令牌的生成、验证和解析功能
 */

@Component
public class JwtUtil {

    /**
     * JWT密钥，从配置文件读取
     * 必须与 SecurityConfig 中使用的密钥保持一致
     */

    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT令牌有效期（毫秒），默认1小时
     */

    @Value("${jwt.expiration:3600000}")
    private Long expiration;

    /**
     * 获取签名密钥
     *
     * @return SecretKey对象
     */

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 根据用户信息生成JWT令牌
     *
     * @param user UserDetails用户详情对象
     * @return 生成的JWT令牌字符串
     */

    public String generateToken(UserDetails user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS384)
                .compact();
    }

    /**
     * 根据认证信息生成JWT令牌
     *
     * @param authentication Spring Security认证对象
     * @return 生成的JWT令牌字符串
     */

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails);
    }
}

