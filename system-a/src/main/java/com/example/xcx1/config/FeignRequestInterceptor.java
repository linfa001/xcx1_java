package com.example.xcx1.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign 请求拦截器
 * 用于在微服务间调用时传递 JWT Token
 */
@Slf4j
@Configuration
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        try {
            // 获取当前请求的上下文
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                // 获取原始请求中的 Authorization header
                String token = attributes.getRequest().getHeader("Authorization");
                if (token != null && !token.isEmpty()) {
                    // 将 Token 传递给目标服务
                    template.header("Authorization", token);
                    log.debug("Feign 请求传递 Token: {}", token.substring(0, Math.min(20, token.length())) + "...");
                } else {
                    log.warn("Feign 请求未找到 Token，可能导致 401 错误");
                }
            } else {
                log.warn("Feign 请求无法获取 RequestContextHolder，可能在异步线程中");
            }
        } catch (Exception e) {
            log.error("Feign 拦截器处理异常", e);
        }
    }
}
