package com.example.sso.filter;

import com.example.sso.config.SsoProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * SSO 认证过滤器
 * 拦截所有请求，校验 JWT Token，校验通过后才放行
 */
@Slf4j
public class SsoAuthenticationFilter implements Filter {

    private final SsoProperties properties;

    public SsoAuthenticationFilter(SsoProperties properties) {
        this.properties = properties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("====== SSO Filter Executed! ======"); // 强制打印
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // 1. 放行登录接口（无需 Token）
        if (requestUri.contains(properties.getLoginUrl())) {
            log.debug("放行登录接口: {} {}", method, requestUri);
            chain.doFilter(request, response);
            return;
        }

        // 2. 获取请求头中的 Token
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("未提供 Token: {} {}", method, requestUri);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        try {
            // 3. 解析并校验 Token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(properties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 4. 将用户信息存入 Request 属性，供后续 Controller 使用
            httpRequest.setAttribute("username", claims.getSubject());
            httpRequest.setAttribute("claims", claims);

            log.debug("Token 校验成功，用户: {}, 路径: {}", claims.getSubject(), requestUri);
            chain.doFilter(request, response);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("Token 已过期: {} {}", method, requestUri);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        } catch (io.jsonwebtoken.JwtException e) {
            log.warn("Token 无效: {} {}, 错误: {}", method, requestUri, e.getMessage());
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        } catch (Exception e) {
            log.error("Token 校验异常: {} {}", method, requestUri, e);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
        }
    }
}
