package com.code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan// 扫描过滤器
@SpringBootApplication
public class TuLingJavaWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(TuLingJavaWebApplication.class, args);
    }

}
