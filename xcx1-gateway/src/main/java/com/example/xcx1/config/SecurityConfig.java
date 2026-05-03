package com.example.xcx1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity;

/**
 * Spring Security 配置
 * 网关层关闭 CSRF，因为我们要使用 JWT 进行无状态认证
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // 关闭 CSRF 保护
                .csrf(csrf -> csrf.disable())
                // 关闭表单登录和 HTTP Basic 认证
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                // 所有请求都放行，由我们自定义的 JwtAuthFilter 进行鉴权
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                .build();
    }
}
