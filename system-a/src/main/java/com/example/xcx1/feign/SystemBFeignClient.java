package com.example.xcx1.feign;

import com.example.xcx1.common.Result;
import com.example.xcx1.entity.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * System-B 服务 Feign 客户端
 * 用于微服务间调用 system-b 的接口
 */
@FeignClient(name = "system-b", path = "/category")
public interface SystemBFeignClient {

    /**
     * 调用 system-b 的 category/getAll 接口
     */
    @GetMapping("/getAll")
    Result<List<Category>> getSystemBCategories();
}
