package com.example.xcx1.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private long total;
    private List<T> records;

    public PageResult() {
    }

    public PageResult(long total, List<T> records) {
        this.total = total;
        this.records = records;
    }
}
