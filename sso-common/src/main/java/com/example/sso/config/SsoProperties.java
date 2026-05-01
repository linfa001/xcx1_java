package com.example.sso.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SSO 配置属性类
 * 用于接收 application.yml 中的 sso 配置项
 */
@Data
@ConfigurationProperties(prefix = "sso")
public class SsoProperties {

    /**
     * 是否启用 SSO 过滤器（默认关闭）
     * 业务系统需在 yml 中设置 sso.enable-filter: true 才能生效
     */
    private boolean enableFilter = true;

    /**
     * JWT 签名密钥
     * 必须与 SSO 认证中心保持一致
     */
    private String secretKey;

    /**
     * 需要排除的登录接口路径（支持包含匹配）
     * 默认排除 /login 开头的请求
     */
    private String loginUrl = "/login";
}
