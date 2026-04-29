/*
package com.example.xcx1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.xcx1.entity.User;
import com.example.xcx1.mapper.UserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

*/
/**
 * 自定义用户详情服务
 * 实现UserDetailsService接口，从数据库加载用户信息进行认证
 *//*

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    */
/**
     * 根据用户名从数据库加载用户信息
     *
     * @param username 用户名
     * @return UserDetails用户详情对象
     * @throws UsernameNotFoundException 当用户不存在时抛出异常
     *//*

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询用户
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 检查用户是否被禁用
        if (user.getEnabled() != null && user.getEnabled() == 0) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 构建 UserDetails 对象
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
*/
