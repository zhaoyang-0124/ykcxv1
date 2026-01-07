package com.jinzhougang.innvotion.ykcxv1;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jinzhougang.innvotion.ykcxv1.mapper")
public class Ykcxv1Application {

    public static void main(String[] args) {
        SpringApplication.run(Ykcxv1Application.class, args);
    }

}
