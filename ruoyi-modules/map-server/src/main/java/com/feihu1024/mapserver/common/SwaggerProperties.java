package com.feihu1024.mapserver.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("swagger")
@Data
public class SwaggerProperties {

    /**
     * 是否开启swagger，生产环境一般关闭，所以这里定义一个变量
     */
    private Boolean enable;

    /**
     * 扫描路径
     */
    private String basePackage;

    /**
     * 项目名称
     */
    private String applicationName;

    /**
     * 文档版本
     */
    private String applicationVersion;

    /**
     * 文档简介
     */
    private String applicationDescription;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文档作者
     * 例: feihu1024,
     */
    private String contact;
}
