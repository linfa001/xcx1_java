package com.example.xcx1.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.xcx1.common.PageResult;
import com.example.xcx1.common.Result;
import com.example.xcx1.dto.ContentSearchRequest;
import com.example.xcx1.entity.Content;
import com.example.xcx1.service.ContentService;
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
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 分页查询所有内容
     */
    @GetMapping("/page")
    public Result<IPage<Content>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        IPage<Content> page = contentService.page(new Page<>(current, size));
        return Result.success(page);
    }

    /**
     * 查询所有内容(带分类名称)
     */
    @GetMapping("/list")
    public Result<List<Content>> list() {
        // 先从Redis缓存中获取
        String cacheKey = "content:list:all";
        List<Content> contents = (List<Content>) redisTemplate.opsForValue().get(cacheKey);
        
        if (contents != null) {
            return Result.success(contents);
        }
        
        // 从数据库查询
        contents = contentService.listWithCategory();
        
        // 存入Redis缓存
        if (contents != null) {
            redisTemplate.opsForValue().set(cacheKey, contents, 30, TimeUnit.MINUTES);
        }
        
        return Result.success(contents);
    }

    /**
     * 根据ID查询内容(带分类名称)
     */
    @GetMapping("/{id}")
    public Result<Content> getById(@PathVariable Integer id) {
        // 先从Redis缓存中获取
        String cacheKey = "content:" + id;
        Content content = (Content) redisTemplate.opsForValue().get(cacheKey);
        
        if (content != null) {
            return Result.success(content);
        }
        
        // 从数据库查询
        content = contentService.getByIdWithCategory(id);
        
        // 存入Redis缓存
        if (content != null) {
            redisTemplate.opsForValue().set(cacheKey, content, 30, TimeUnit.MINUTES);
        }
        
        return Result.success(content);
    }

    /**
     * 新增内容
     */
    @PostMapping
    public Result<Boolean> save(@RequestBody Content content) {
        boolean result = contentService.save(content);
        // 清除列表缓存
        if (result) {
            redisTemplate.delete("content:list:all");
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
            redisTemplate.delete("content:" + content.getId());
            redisTemplate.delete("content:list:all");
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
            redisTemplate.delete("content:" + id);
            redisTemplate.delete("content:list:all");
            return Result.success(true, "删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 根据分类ID查询内容列表
     */
    @GetMapping("/category/{categoryId}")
    public Result<List<Content>> getByCategoryId(@PathVariable Integer categoryId) {
        String cacheKey = "content:category:" + categoryId;
        List<Content> contents = (List<Content>) redisTemplate.opsForValue().get(cacheKey);
        
        if (contents != null) {
            return Result.success(contents);
        }
        
        contents = contentService.listByCategoryId(categoryId);
        
        if (contents != null) {
            redisTemplate.opsForValue().set(cacheKey, contents, 30, TimeUnit.MINUTES);
        }
        
        return Result.success(contents);
    }

    /**
     * 根据标题搜索内容（GET方式，兼容旧接口）
     */
    @GetMapping("/search")
    public Result<List<Content>> searchByTitle(@RequestParam(required = false) String title) {
        List<Content> contents;
        if (StrUtil.isBlank(title)) {
            contents = contentService.list();
        } else {
            contents = contentService.lambdaQuery()
                    .like(Content::getTitle, title)
                    .list();
        }
        return Result.success(contents);
    }

    /**
     * 搜索内容（POST方式，支持分页和分类筛选）
     * 请求地址: POST /api/content/search?page=1&limit=10
     * Content-Type: application/json
     * 请求参数: {"q":"","categoryId":0}
     */
    @PostMapping(value = "/search", consumes = "application/json", produces = "application/json")
    public PageResult<Content> search(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestBody(required = false) ContentSearchRequest request) {
        
        if (request == null) {
            request = new ContentSearchRequest();
        }
        
        // 优先使用 URL 查询参数，若为空则回退到 JSON Body 中的值
        int currentPage = page != null ? page : (request.getPage() != null ? request.getPage() : 1);
        int currentLimit = limit != null ? limit : (request.getLimit() != null ? request.getLimit() : 10);
        
        // 构建查询条件
        QueryWrapper<Content> queryWrapper = new QueryWrapper<>();
        
        // 关键词搜索（搜索标题）
        if (StrUtil.isNotBlank(request.getQ())) {
            queryWrapper.like("title", request.getQ());
        }
        
        // 分类筛选
        if (request.getCategoryId() != null && request.getCategoryId() > 0) {
            queryWrapper.eq("category_id", request.getCategoryId());
        }
        
        // 分页查询
        Page<Content> pageParam = new Page<>(currentPage, currentLimit);
        IPage<Content> resultPage = contentService.page(pageParam, queryWrapper);
        
        // 封装返回结果
        return PageResult.of(
                resultPage.getRecords(),
                resultPage.getTotal(),
                currentPage,
                currentLimit
        );
    }

    /**
     * 根据作者搜索内容
     */
    @GetMapping("/author/{author}")
    public Result<List<Content>> getByAuthor(@PathVariable String author) {
        List<Content> contents;
        if (StrUtil.isBlank(author)) {
            contents = contentService.list();
        } else {
            contents = contentService.lambdaQuery()
                    .like(Content::getAuthor, author)
                    .list();
        }
        return Result.success(contents);
    }

    /**
     * 获取热门内容(示例:按ID倒序取前10条)
     */
    @GetMapping("/hot")
    public Result<List<Content>> getHotContents() {
        String cacheKey = "content:hot";
        List<Content> contents = (List<Content>) redisTemplate.opsForValue().get(cacheKey);
        
        if (contents != null) {
            return Result.success(contents);
        }
        
        contents = contentService.lambdaQuery()
                .orderByDesc(Content::getId)
                .last("LIMIT 10")
                .list();
        
        if (contents != null) {
            redisTemplate.opsForValue().set(cacheKey, contents, 1, TimeUnit.HOURS);
        }
        
        return Result.success(contents);
    }

    /**
     * 清除所有内容缓存（调试用）
     */
    @PostMapping("/clear-cache")
    public Result<String> clearCache() {
        // 清除所有以 content: 开头的键
        java.util.Set<String> keys = redisTemplate.keys("content:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return Result.success("缓存已清除");
    }
}
