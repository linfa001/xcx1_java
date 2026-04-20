package com.example.xcx1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 内容实体类
 */
@Data
@TableName("contents")
public class Content implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 日期
     */
    @JsonProperty("date")
    private LocalDateTime date;

    /**
     * 图片URL
     */
    private String image;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 创建时间
     */
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    /**
     * 关联的分类名称(非数据库字段)
     */
    @TableField(exist = false)
    private String categoryName;
}
