//package com.example.xcx1.exception;
//
//import com.example.xcx1.common.Result;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.catalina.connector.ClientAbortException;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.io.IOException;
//import java.net.SocketTimeoutException;
//
///**
// * 全局异常处理器
// */
//@Slf4j
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    /**
//     * 处理客户端中断异常（写入响应时断开）
//     */
//    @ExceptionHandler(ClientAbortException.class)
//    public void handleClientAbortException(ClientAbortException e) {
//        // 客户端主动断开连接，不打印错误日志
//        log.warn("客户端已断开连接（响应阶段）");
//    }
//
//    /**
//     * 处理 IO 异常（读取请求时断开）
//     */
//    @ExceptionHandler(IOException.class)
//    public void handleIOException(IOException e) {
//        String message = e.getMessage();
//        if (isClientDisconnect(message)) {
//            log.warn("客户端已断开连接（请求阶段）");
//        } else {
//            log.error("IO异常: ", e);
//        }
//    }
//
//    /**
//     * 处理请求体解析异常（通常是客户端发送了无效的 JSON 或中途断开）
//     */
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public Result<String> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
//        // 优先检查是否为客户端断开或超时
//        if (isClientDisconnectException(e)) {
//            log.warn("客户端已断开连接或请求超时（解析请求体时）");
//            // 客户端已断开，不再返回响应，避免二次异常
//            return null;
//        }
//        // 其他情况通常是 JSON 格式错误，需要返回错误信息
//        log.warn("请求参数格式错误: {}", e.getMessage());
//        return Result.error("请求参数格式错误");
//    }
//
//    /**
//     * 处理其他异常
//     */
//    @ExceptionHandler(Exception.class)
//    public Result<String> handleException(Exception e) {
//        // 忽略客户端断开相关的异常，不返回响应
//        if (isClientDisconnectException(e)) {
//            log.warn("客户端已断开连接");
//            return null;
//        }
//        // 其他异常记录错误日志并返回错误信息
//        log.error("系统异常: ", e);
//        return Result.error("系统异常: " + e.getMessage());
//    }
//
//    /**
//     * 判断异常是否由客户端断开连接引起
//     */
//    private boolean isClientDisconnectException(Exception e) {
//        // 优先通过异常类型判断（更可靠）
//        if (e instanceof ClientAbortException || e instanceof SocketTimeoutException) {
//            return true;
//        }
//        if (e instanceof IOException && isClientDisconnect(e.getMessage())) {
//            return true;
//        }
//
//        // 检查嵌套的 cause
//        Throwable cause = e.getCause();
//        while (cause != null) {
//            if (cause instanceof ClientAbortException || cause instanceof SocketTimeoutException) {
//                return true;
//            }
//            if (cause instanceof IOException && isClientDisconnect(cause.getMessage())) {
//                return true;
//            }
//            cause = cause.getCause();
//        }
//        return false;
//    }
//
//    /**
//     * 判断错误消息是否表示客户端断开连接
//     * 注意：由于编码问题，中文可能显示为乱码
//     */
//    private boolean isClientDisconnect(String message) {
//        if (message == null) return false;
//        String lowerMsg = message.toLowerCase();
//        // 英文关键词
//        if (lowerMsg.contains("timeout")
//                || lowerMsg.contains("reset")
//                || lowerMsg.contains("broken pipe")
//                || lowerMsg.contains("abort")
//                || lowerMsg.contains("connection reset")
//                || lowerMsg.contains("closed")) {
//            return true;
//        }
//        // 中文字符（正常编码）
//        if (message.contains("远程")
//                || message.contains("现有连接")
//                || message.contains("强迫")
//                || message.contains("强制")
//                || message.contains("关闭")
//                || message.contains("中断")) {
//            return true;
//        }
//        return false;
//    }
//}
