package com.example.xcx1.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xcx1.entity.Content;

import java.util.List;

/**
 * 内容Service接口
 */
public interface ContentService extends IService<Content> {

    /**
     * 分页查询所有内容(带分类名称)
     */
    IPage<Content> pageWithCategory(int current, int size);

    /**
     * 查询所有内容(带分类名称)
     */
    List<Content> listWithCategory();

    /**
     * 根据ID查询内容(带分类名称)
     */
    Content getByIdWithCategory(Integer id);

    /**
     * 根据分类ID查询内容列表
     */
    List<Content> listByCategoryId(Integer categoryId);
}
