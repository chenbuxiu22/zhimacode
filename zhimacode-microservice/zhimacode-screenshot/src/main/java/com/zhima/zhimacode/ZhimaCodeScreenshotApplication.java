package com.zhima.zhimacode;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ZhimaCodeScreenshotApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZhimaCodeScreenshotApplication.class, args);
    }
}