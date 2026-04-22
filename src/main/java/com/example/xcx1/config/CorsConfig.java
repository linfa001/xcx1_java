package com.example.xcx1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许所有来源，生产环境建议指定具体域名
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://127.0.0.1:3000");
        config.addAllowedOrigin("https://recant-oboe-muppet.ngrok-free.dev/api/category/getAl");
//        config.addAllowedOrigin("http://127.0.0.1:3001");
        
        // 是否允许发送Cookie
        config.setAllowCredentials(true);
        
        // 允许的请求方法
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        
        // 允许的请求头
        config.addAllowedHeader("Content-Type");
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("X-Requested-With");
        config.addAllowedHeader("ngrok-skip-browser-warning");
        config.addAllowedHeader("User-Agent");


        config.setAllowCredentials(true);

        // 暴露的响应头
        config.addExposedHeader("Content-Disposition");
        config.addExposedHeader("Content-Length");
        config.addExposedHeader("X-Total-Count");
        
        // 预检请求的有效期（秒）
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径生效
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
