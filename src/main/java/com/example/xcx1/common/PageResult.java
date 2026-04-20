package com.example.xcx1.common;

import lombok.Data;
import java.util.List;

/**
 * 分页响应结果类
 */
@Data
public class PageResult<T> {
    
    private Boolean success = true;
    private List<T> data;
    private Long total;
    private Integer page;
    private Integer limit;

    public PageResult() {
    }

    public PageResult(List<T> data, Long total, Integer page, Integer limit) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.limit = limit;
    }

    public static <T> PageResult<T> of(List<T> data, Long total, Integer page, Integer limit) {
        return new PageResult<>(data, total, page, limit);
    }
}
