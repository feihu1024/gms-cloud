package com.feihu1024.mapserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan("com.feihu1024.mapserver.interrupt")
@MapperScan("com.feihu1024.mapserver.mapper")
public class MapServerApplication {
    public static void main(String[] args){
        SpringApplication.run(MapServerApplication.class, args);
    }
}

/**
 * 需要添加nacos的配置
 */