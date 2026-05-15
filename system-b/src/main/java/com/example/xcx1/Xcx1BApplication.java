package com.example.xcx1;

import com.example.sso.config.SsoAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * 应用启动类 - System B
 */
@SpringBootApplication
@MapperScan("com.example.xcx1.mapper")
@Import(SsoAutoConfiguration.class)
public class Xcx1BApplication {

    public static void main(String[] args) {
        SpringApplication.run(Xcx1BApplication.class, args);
        System.out.println("===========================================");
        System.out.println("   System-B Application Started!");
        System.out.println("===========================================");
    }
}
