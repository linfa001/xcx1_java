package com.example.xcx1.common;

import lombok.Data;

/**
 * 统一响应结果类
 */
@Data
public class Result<T> {
    
    private Boolean success;
    private T data;
    private String message;

    public Result() {
    }

    public Result(Boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public Result(Boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, data);
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(true, data, message);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(false, null, message);
    }
}
