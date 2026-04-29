/*
package com.example.xcx1.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

*/
/**
 * 密码生成工具
 * 用于生成 BCrypt 加密的密码
 *//*

public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 生成测试用户密码
        String adminPassword = encoder.encode("123456");
        String userPassword = encoder.encode("password");

        System.out.println("=== BCrypt 密码生成结果 ===");
        System.out.println("admin 用户 (密码: 123456):");
        System.out.println(adminPassword);
        System.out.println();
        System.out.println("user 用户 (密码: password):");
        System.out.println(userPassword);
        System.out.println();
        System.out.println("=== 对应的 SQL 插入语句 ===");
        System.out.println("INSERT INTO user (username, password, enabled) VALUES");
        System.out.println("('admin', '" + adminPassword + "', 1),");
        System.out.println("('user', '" + userPassword + "', 1);");
    }
}
*/
