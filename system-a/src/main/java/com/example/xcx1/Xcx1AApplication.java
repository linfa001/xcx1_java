package com.example.xcx1;

import com.example.sso.config.SsoAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/**
 * 应用启动类
 */
@SpringBootApplication
@MapperScan("com.example.xcx1.mapper")
@Import(SsoAutoConfiguration.class)
@EnableFeignClients
public class Xcx1AApplication {

    public static void main(String[] args) {
        SpringApplication.run(Xcx1AApplication.class, args);
        System.out.println("===========================================");
        System.out.println("   Spring Boot Application Started!");
        System.out.println("===========================================");
    }
}
