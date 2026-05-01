package com.example.sso.config;

import com.example.sso.filter.SsoAuthenticationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SSO 自动配置类
 * 只有当配置文件中 sso.enable-filter=true 时，才会注册过滤器
 */
@Configuration
@EnableConfigurationProperties(SsoProperties.class)
@ConditionalOnProperty(prefix = "sso", name = "enable-filter", havingValue = "true")
public class SsoAutoConfiguration {

    /**
     * 注册 SSO 认证过滤器
     * 拦截所有请求，优先级设为最高（Order=1）
     */
    @Bean
    public FilterRegistrationBean<SsoAuthenticationFilter> ssoAuthenticationFilter(SsoProperties properties) {
        FilterRegistrationBean<SsoAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SsoAuthenticationFilter(properties));
        registration.addUrlPatterns("/*");
        registration.setName("ssoAuthenticationFilter");
        registration.setOrder(1);
        return registration;
    }
}
