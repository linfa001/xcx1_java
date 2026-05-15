package com.example.xcx1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xcx1.entity.Content;
import com.example.xcx1.mapper.ContentMapper;
import com.example.xcx1.service.ContentService;
import org.springframework.stereotype.Service;

/**
 * 内容Service实现
 */
@Service
public class ContentServiceImpl extends ServiceImpl<ContentMapper, Content> implements ContentService {
}
