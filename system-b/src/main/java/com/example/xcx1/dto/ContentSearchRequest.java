package com.example.xcx1.dto;

import lombok.Data;

/**
 * 内容搜索请求DTO
 */
@Data
public class ContentSearchRequest {
    private String title;
    private Integer categoryId;
}