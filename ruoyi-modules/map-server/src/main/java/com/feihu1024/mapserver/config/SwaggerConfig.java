package com.feihu1024.mapserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * 手动向 OpenAPI 文档中添加 /oauth/token 接口
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // 构建登录操作
        Operation loginOperation = new Operation()
                .summary("登录")
                .description("用户登陆获取 token")
                .addTagsItem("权限管理服务")
                .addParametersItem(new QueryParameter().name("client_id").description("客户端id").required(true).schema(new StringSchema()))
                .addParametersItem(new QueryParameter().name("client_secret").description("客户端密码").required(true).schema(new StringSchema()))
                .addParametersItem(new QueryParameter().name("grant_type").description("登录类型").required(true).schema(new StringSchema()))
                .addParametersItem(new QueryParameter().name("username").description("用户名").required(true).schema(new StringSchema()))
                .addParametersItem(new QueryParameter().name("password").description("密码").required(true).schema(new StringSchema()));

        // 创建 PathItem 并设置 POST 方法
        PathItem pathItem = new PathItem();
        pathItem.setPost(loginOperation);

        // 构建 OpenAPI 对象并注册路径
        return new OpenAPI().path("/oauth/token", pathItem);
    }
}
