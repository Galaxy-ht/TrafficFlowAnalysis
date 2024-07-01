package com.egon.analysis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.egon.analysis.mappers")
@EnableCaching
public class TrafficAnalysisApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrafficAnalysisApplication.class, args);
    }
}
