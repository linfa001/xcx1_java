package com.example.xcx1.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.xcx1.common.PageResult;
import com.example.xcx1.common.Result;
import com.example.xcx1.dto.ContentSearchRequest;
import com.example.xcx1.entity.Content;
import com.example.xcx1.entity.Category;
import com.example.xcx1.service.ContentService;
import com.example.xcx1.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 内容Controller
 */
@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 查询所有内容（带分类名称）
     */
    @GetMapping("/getAll")
    public Result<List<Content>> getAll() {
        // 先从Redis缓存中获取
        String cacheKey = "content:list:b";
        List<Content> contents = (List<Content>) redisTemplate.opsForValue().get(cacheKey);
        
        if (contents != null) {
            return Result.success(contents);
        }
        
        // 从数据库查询
        contents = contentService.list();
        
        // 填充分类名称
        fillCategoryName(contents);
        
        // 存入Redis缓存
        if (contents != null) {
            redisTemplate.opsForValue().set(cacheKey, contents, 30, TimeUnit.MINUTES);
        }
        
        return Result.success(contents);
    }

    /**
     * 分页查询内容
     */
    @GetMapping("/page")
    public Result<PageResult<Content>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        // 先从Redis缓存中获取
        String cacheKey = "content:page:b:" + current + ":" + size;
        PageResult<Content> pageResult = (PageResult<Content>) redisTemplate.opsForValue().get(cacheKey);
        
        if (pageResult != null) {
            return Result.success(pageResult);
        }
        
        // 构建分页对象
        Page<Content> page = new Page<>(current, size);
        page = contentService.page(page);
        
        // 填充分类名称
        fillCategoryName(page.getRecords());
        
        pageResult = new PageResult<>(page.getTotal(), page.getRecords());
        
        // 存入Redis缓存
        redisTemplate.opsForValue().set(cacheKey, pageResult, 30, TimeUnit.MINUTES);
        
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询内容
     */
    @GetMapping("/{id}")
    public Result<Content> getById(@PathVariable Integer id) {
        // 先从Redis缓存中获取
        String cacheKey = "content:b:" + id;
        Content content = (Content) redisTemplate.opsForValue().get(cacheKey);
        
        if (content != null) {
            return Result.success(content);
        }
        
        // 从数据库查询
        content = contentService.getById(id);
        
        if (content != null) {
            // 填充分类名称
            fillCategoryName(List.of(content));
            
            // 存入Redis缓存
            redisTemplate.opsForValue().set(cacheKey, content, 30, TimeUnit.MINUTES);
        }
        
        return Result.success(content);
    }

    /**
     * 根据分类ID查询内容
     */
    @GetMapping("/category/{categoryId}")
    public Result<List<Content>> getByCategoryId(@PathVariable Integer categoryId) {
        List<Content> contents = contentService.lambdaQuery()
                .eq(Content::getCategoryId, categoryId)
                .list();
        
        fillCategoryName(contents);
        
        return Result.success(contents);
    }

    /**
     * 搜索内容
     */
    @PostMapping("/search")
    public Result<List<Content>> search(@RequestBody ContentSearchRequest request) {
        List<Content> contents;
        
        if (StrUtil.isBlank(request.getTitle()) && request.getCategoryId() == null) {
            contents = contentService.list();
        } else {
            var query = contentService.lambdaQuery();
            
            if (StrUtil.isNotBlank(request.getTitle())) {
                query.like(Content::getTitle, request.getTitle());
            }
            if (request.getCategoryId() != null) {
                query.eq(Content::getCategoryId, request.getCategoryId());
            }
            
            contents = query.list();
        }
        
        fillCategoryName(contents);
        
        return Result.success(contents);
    }

    /**
     * 新增内容
     */
    @PostMapping
    public Result<Boolean> save(@RequestBody Content content) {
        boolean result = contentService.save(content);
        // 清除缓存
        if (result) {
            clearContentCache();
            return Result.success(true, "添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 更新内容
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody Content content) {
        boolean result = contentService.updateById(content);
        // 清除缓存
        if (result && content.getId() != null) {
            redisTemplate.delete("content:b:" + content.getId());
            clearContentCache();
            return Result.success(true, "更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除内容
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        boolean result = contentService.removeById(id);
        // 清除缓存
        if (result) {
            redisTemplate.delete("content:b:" + id);
            clearContentCache();
            return Result.success(true, "删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 清除所有内容缓存
     */
    @PostMapping("/clear-cache")
    public Result<String> clearCache() {
        clearContentCache();
        return Result.success("缓存已清除");
    }

    /**
     * 填充分类名称
     */
    private void fillCategoryName(List<Content> contents) {
        if (contents == null || contents.isEmpty()) {
            return;
        }
        
        // 获取所有分类
        List<Category> categories = categoryService.list();
        Map<Integer, String> categoryMap = new HashMap<>();
        for (Category category : categories) {
            categoryMap.put(category.getId(), category.getName());
        }
        
        // 填充分类名称
        for (Content content : contents) {
            if (content.getCategoryId() != null) {
                content.setCategoryName(categoryMap.get(content.getCategoryId()));
            }
        }
    }

    /**
     * 清除内容缓存
     */
    private void clearContentCache() {
        java.util.Set<String> keys = redisTemplate.keys("content:b:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
