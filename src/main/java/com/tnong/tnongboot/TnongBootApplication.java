package com.tnong.tnongboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.tnong.boot", "com.tnong.tnongboot"})
@MapperScan("com.tnong.boot.**.mapper")
public class TnongBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(TnongBootApplication.class, args);
    }

}
