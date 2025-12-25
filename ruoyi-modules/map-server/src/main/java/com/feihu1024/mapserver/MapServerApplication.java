package com.feihu1024.mapserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@ServletComponentScan("com.feihu1024.mapserver.interrupt")
@MapperScan("com.feihu1024.mapserver.mapper")
@EnableCaching
public class MapServerApplication {
    public static void main(String[] args){
        SpringApplication.run(MapServerApplication.class, args);
    }
}