/*
package com.example.xcx1.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

*/
/**
 * 请求日志过滤器 - 用于捕获请求阶段的客户端断开异常
 *//*

@Slf4j
@Component
@Order(1)
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (IOException e) {
            // 捕获客户端断开连接异常，完全吞掉不继续传播
            if (isClientDisconnect(e.getMessage())) {
                HttpServletRequest req = (HttpServletRequest) request;
                log.warn("客户端已断开连接: {} {}", req.getMethod(), req.getRequestURI());
                // 不继续抛出，直接返回
                return;
            }
            throw e;
        } catch (RuntimeException e) {
            // 检查是否是包装的 IOException
            if (e.getCause() instanceof IOException && isClientDisconnect(e.getCause().getMessage())) {
                HttpServletRequest req = (HttpServletRequest) request;
                log.warn("客户端已断开连接: {} {}", req.getMethod(), req.getRequestURI());
                return;
            }
            throw e;
        }
    }

    private boolean isClientDisconnect(String message) {
        if (message == null) return false;
        return message.contains("Connection reset")
                || message.contains("Broken pipe")
                || message.contains("Connection timed out")
                || message.contains("abort")
                || message.contains("ClientAbort");
    }
}
*/
