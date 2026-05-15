package com.example.xcx1.controller;

import com.example.xcx1.common.Result;
import com.example.xcx1.entity.Category;
import com.example.xcx1.feign.SystemBFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    private SystemBFeignClient systemBFeignClient;

    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello, World!");
    }

    /**
     * 测试微服务间调用：通过 OpenFeign 调用 system-b 的 category/getAll 接口
     */
    @GetMapping("/test-feign")
    public Result<Map<String, Object>> testFeignCall() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 通过 OpenFeign 调用 system-b 服务
            Result<List<Category>> response = systemBFeignClient.getSystemBCategories();
            
            result.put("success", true);
            result.put("message", "成功调用 system-b 服务");
            result.put("data", response.getData());
            result.put("service", "system-b");
            
            return Result.success(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "调用 system-b 服务失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            
            return Result.error("微服务调用失败: " + e.getMessage());
        }
    }

}
