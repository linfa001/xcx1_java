package com.example.xcx1;

import com.example.sso.config.SsoAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * 应用启动类
 */
@SpringBootApplication
@MapperScan("com.example.xcx1.mapper")
@Import(SsoAutoConfiguration.class)
public class Xcx1Application {

    public static void main(String[] args) {
        SpringApplication.run(Xcx1Application.class, args);
        System.out.println("===========================================");
        System.out.println("   Spring Boot Application Started!");
        System.out.println("===========================================");
    }
}
