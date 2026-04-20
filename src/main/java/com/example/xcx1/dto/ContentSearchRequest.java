package com.example.xcx1.dto;

import lombok.Data;

/**
 * 内容搜索请求参数
 */
@Data
public class ContentSearchRequest {
    
    /**
     * 搜索关键词
     */
    private String q;
    
    /**
     * 分类ID
     */
    private Integer categoryId;
    
    /**
     * 页码，默认1
     */
    private Integer page = 1;
    
    /**
     * 每页数量，默认10
     */
    private Integer limit = 10;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
