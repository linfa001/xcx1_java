package com.example.xcx1.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xcx1.entity.Content;
import com.example.xcx1.mapper.ContentMapper;
import com.example.xcx1.service.ContentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 内容Service实现类
 */
@Service
public class ContentServiceImpl extends ServiceImpl<ContentMapper, Content> implements ContentService {

    @Override
    public IPage<Content> pageWithCategory(int current, int size) {
        // MyBatis-Plus的分页需要配合XML或自定义SQL,这里先返回基础分页
        // 如需关联查询,建议使用XML方式
        Page<Content> page = new Page<>(current, size);
        return this.page(page);
    }

    @Override
    public List<Content> listWithCategory() {
        return this.getBaseMapper().selectAllWithCategory();
    }

    @Override
    public Content getByIdWithCategory(Integer id) {
        return this.getBaseMapper().selectByIdWithCategory(id);
    }

    @Override
    public List<Content> listByCategoryId(Integer categoryId) {
        return this.getBaseMapper().selectByCategoryId(categoryId);
    }
}
