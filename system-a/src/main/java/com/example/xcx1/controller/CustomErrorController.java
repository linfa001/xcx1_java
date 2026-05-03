package com.example.xcx1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 自定义错误控制器
 * 用于优雅地处理错误页面渲染时的客户端断开连接
 */
@Slf4j
@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request) {
        // 获取错误状态码
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        // 如果是客户端断开连接，不记录错误日志
        if (statusCode != null) {
            if (statusCode == HttpStatus.NOT_FOUND.value() || 
                statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                log.debug("客户端请求错误: {}", statusCode);
            }
        }
        
        // 返回空响应，避免写入错误页面时再次触发客户端断开连接
        return null;
    }
}
