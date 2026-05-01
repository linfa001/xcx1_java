package com.example.xcx1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xcx1.entity.Category;
import com.example.xcx1.mapper.CategoryMapper;
import com.example.xcx1.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * 分类Service实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    
}
