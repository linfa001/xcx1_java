package com.example.xcx1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security配置类
 * 配置OAuth2 Resource Server和JWT认证
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * JWT签名密钥，从配置文件读取
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * 配置登录接口的安全策略（公开访问，无需认证）
     * 优先级设置为 0，确保优先匹配
     *
     * @param http HttpSecurity对象
     * @return SecurityFilterChain实例
     * @throws Exception 配置异常
     */
    @Bean
    @Order(0)
    public SecurityFilterChain loginSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .antMatcher("/login/**")
            .authorizeRequests(auth -> auth
                .anyRequest().permitAll()
            )
            // 明确禁用 OAuth2，防止携带 token 的请求被拦截
            .oauth2ResourceServer().disable()
            .csrf().disable()
            // 允许跨域
            .cors().disable();
        return http.build();
    }

    /**
     * 配置安全过滤链
     * 使用OAuth2 Resource Server模式处理JWT认证
     * 排除登录接口
     *
     * @param http HttpSecurity对象
     * @return SecurityFilterChain实例
     * @throws Exception 配置异常
     */
    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 排除登录接口，只匹配其他所有请求
            .requestMatcher(new NegatedRequestMatcher(new AntPathRequestMatcher("/login/**")))
            .authorizeRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .csrf().disable();
        return http.build();
    }

    /**
     * 配置认证管理器
     * 提供 AuthenticationManager Bean 供登录接口使用
     *
     * @param authenticationConfiguration Spring Security认证配置
     * @return AuthenticationManager实例
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 配置密码编码器
     * 使用BCrypt强哈希算法加密密码
     *
     * @return PasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置JWT认证转换器
     * 将JWT令牌转换为Spring Security认证对象
     * 正确处理 roles claim
     *
     * @return JwtAuthenticationConverter实例
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new Converter<Jwt, Collection<GrantedAuthority>>() {
            @Override
            public Collection<GrantedAuthority> convert(Jwt jwt) {
                // 从 JWT 的 roles claim 中提取权限
                List<String> roles = jwt.getClaimAsStringList("roles");
                if (roles == null) {
                    return Collections.emptyList();
                }
                return roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            }
        });
        return converter;
    }

    /**
     * 配置JWT解码器
     * 用于验证和解析JWT令牌
     * 必须使用与 JwtUtil 相同的密钥生成方式
     *
     * @return JwtDecoder实例
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        // 使用与 JJWT 相同的密钥处理方式
        SecretKey key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(
            jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
        // 显式指定算法为 HS384，与 JwtUtil 保持一致
        return NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS384)
                .build();
    }
}
