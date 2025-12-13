package com.tnong.tnongboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tnong.boot.**.mapper")
public class TnongBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(TnongBootApplication.class, args);
    }

}
