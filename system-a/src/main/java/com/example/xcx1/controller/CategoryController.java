package com.example.xcx1.controller;

import cn.hutool.core.util.StrUtil;
import com.example.xcx1.common.Result;
import com.example.xcx1.entity.Category;
import com.example.xcx1.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 分类Controller
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 查询所有分类（兼容 /api/categories 路径）
     */
    @GetMapping("/getAll")
    public Result<List<Category>> getCategories() {
        // 先从Redis缓存中获取
        String cacheKey = "category:list";
        List<Category> categories = (List<Category>) redisTemplate.opsForValue().get(cacheKey);
        
        if (categories != null) {
            return Result.success(categories);
        }
        
        // 从数据库查询
        categories = categoryService.list();
        
        // 存入Redis缓存，设置过期时间为30分钟
        if (categories != null) {
            redisTemplate.opsForValue().set(cacheKey, categories, 30, TimeUnit.MINUTES);
        }
        
        return Result.success(categories);
    }

    /**
     * 查询所有分类
     */
    @GetMapping("/list")
    public Result<List<Category>> list() {
        // 先从Redis缓存中获取
        String cacheKey = "category:list";
        List<Category> categories = (List<Category>) redisTemplate.opsForValue().get(cacheKey);
        
        if (categories != null) {
            return Result.success(categories);
        }
        
        // 从数据库查询
        categories = categoryService.list();
        
        // 存入Redis缓存，设置过期时间为30分钟
        if (categories != null) {
            redisTemplate.opsForValue().set(cacheKey, categories, 30, TimeUnit.MINUTES);
        }
        
        return Result.success(categories);
    }

    /**
     * 根据ID查询分类
     */
    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Integer id) {
        // 先从Redis缓存中获取
        String cacheKey = "category:" + id;
        Category category = (Category) redisTemplate.opsForValue().get(cacheKey);
        
        if (category != null) {
            return Result.success(category);
        }
        
        // 从数据库查询
        category = categoryService.getById(id);
        
        // 存入Redis缓存，设置过期时间为30分钟
        if (category != null) {
            redisTemplate.opsForValue().set(cacheKey, category, 30, TimeUnit.MINUTES);
        }
        
        return Result.success(category);
    }

    /**
     * 新增分类
     */
    @PostMapping
    public Result<Boolean> save(@RequestBody Category category) {
        boolean result = categoryService.save(category);
        // 清除列表缓存
        if (result) {
            redisTemplate.delete("category:list");
            return Result.success(true, "添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 更新分类
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody Category category) {
        boolean result = categoryService.updateById(category);
        // 清除缓存
        if (result && category.getId() != null) {
            redisTemplate.delete("category:" + category.getId());
            redisTemplate.delete("category:list");
            return Result.success(true, "更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        boolean result = categoryService.removeById(id);
        // 清除缓存
        if (result) {
            redisTemplate.delete("category:" + id);
            redisTemplate.delete("category:list");
            return Result.success(true, "删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 根据名称查询分类
     */
    @GetMapping("/search")
    public Result<List<Category>> searchByName(@RequestParam String name) {
        List<Category> categories;
        if (StrUtil.isBlank(name)) {
            categories = categoryService.list();
        } else {
            categories = categoryService.lambdaQuery()
                    .like(Category::getName, name)
                    .list();
        }
        return Result.success(categories);
    }

    /**
     * 清除所有分类缓存（调试用）
     */
    @PostMapping("/clear-cache")
    public Result<String> clearCache() {
        // 清除所有以 category: 开头的键
        java.util.Set<String> keys = redisTemplate.keys("category:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return Result.success("缓存已清除");
    }
}
