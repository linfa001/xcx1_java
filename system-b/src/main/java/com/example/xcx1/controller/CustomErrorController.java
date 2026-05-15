package com.example.xcx1.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义错误处理Controller
 */
@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public Map<String, Object> error(HttpServletRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", new java.util.Date());
        error.put("status", request.getAttribute("javax.servlet.error.status_code"));
        error.put("error", request.getAttribute("javax.servlet.error.message"));
        error.put("path", request.getAttribute("javax.servlet.error.request_uri"));
        return error;
    }
}
