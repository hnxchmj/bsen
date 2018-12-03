package com.nbcb.myron.bsen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nbcb.myron.bsen.dao")
public class BsenApplication {

    public static void main(String[] args) {
        SpringApplication.run(BsenApplication.class, args);
    }
}
