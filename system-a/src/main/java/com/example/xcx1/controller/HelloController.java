package com.example.xcx1.controller;

import com.example.xcx1.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello, World!");
    }

}
