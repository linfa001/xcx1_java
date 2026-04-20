package com.example.xcx1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.xcx1.entity.Content;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 内容Mapper接口
 */
@Mapper
public interface ContentMapper extends BaseMapper<Content> {

    /**
     * 查询所有内容并关联分类名称
     */
    @Select("SELECT c.*, cat.name as category_name FROM contents c LEFT JOIN categories cat ON c.category_id = cat.id")
    List<Content> selectAllWithCategory();

    /**
     * 根据ID查询内容并关联分类名称
     */
    @Select("SELECT c.*, cat.name as category_name FROM contents c LEFT JOIN categories cat ON c.category_id = cat.id WHERE c.id = #{id}")
    Content selectByIdWithCategory(Integer id);

    /**
     * 根据分类ID查询内容列表
     */
    @Select("SELECT c.*, cat.name as category_name FROM contents c LEFT JOIN categories cat ON c.category_id = cat.id WHERE c.category_id = #{categoryId}")
    List<Content> selectByCategoryId(Integer categoryId);
}
