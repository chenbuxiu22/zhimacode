package com.zhima.zhimacodeuser;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDubbo
@MapperScan("com.zhima.zhimacodeuser.mapper")
@ComponentScan("com.zhima")
public class ZhimaCodeUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZhimaCodeUserApplication.class, args);
    }
}