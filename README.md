## 一、关于Redis

临时启动Redis
```angular2html
redis-server.exe redis.windows.conf
```

Redis常用的服务指令
```angular2html
卸载服务：redis-server --service-uninstall
开启服务：redis-server --service-start
停止服务：redis-server --service-stop
```

使用Redis客户端验证服务是否正常
```angular2html
redis-cli.exe -h 127.0.0.1 -p 6379
```
连接成功后使用ping命令来检测redis服务器与redis客户端的连通性，返回PONG则说明连接成功。



## 二、关于nacos

若依PG版gitlee地址：https://gitee.com/suxia2/nacos-postgresql-2.5.0

单机启动命令

```
startup.cmd -m standalone
```



1. 存在问题：发布了配置以后历史记录中没有对应的
2. 表结构已经搞清楚了，现在的问题是his_config_info表中id字段没有自增，没有默认值，但nacos程序中没有正确传入id字段导致编辑后保存操作报错，准备重新编译一版2.5.0的nacos进行测试，必要时修改一下对应的his_config_info的接口代码

## 三、关于若依系统

### 1. swagger访问地址

新版swagger访问地址： http://localhost:8080/doc.html

### 2. 启动项目

必须启动的三个服务：ruoyi-system、ruoyi-gateway、ruoyi-auth

用户名和密码：admin		admin123

### 3. 为新模块添加swagger文档

   1. 在gateway的knife4j配置中添加对应模块的routes配置
	```
      # springdoc配置
      knife4j:
        gateway:
          enabled: true
          tags-sorter: order
          operations-sorter: order
          strategy: manual
          routes:
            - name: 系统模块
              url: /ruoyi-system/v3/api-docs?group=default
              service-name: ruoyi-system
              context-path: /ruoyi-system
              order: 1
            # 希望添加的模块
            - name: 认证模块
              url: /ruoyi-auth/v3/api-docs?group=default
              service-name: ruoyi-auth
              context-path: /ruoyi-auth
              order: 2
   ```
   2. 在对应模块的pom文件中添加: ruoyi-common-swagger依赖

   ```
      <!-- RuoYi Common Swagger -->
              <dependency>
                  <groupId>com.ruoyi</groupId>
                  <artifactId>ruoyi-common-swagger</artifactId>
              </dependency>
      ```

      2. 重启服务。（未验证，最好是整个重启一遍）

## 四待解决问题

1. nacos存在问题，当在nacos中修改了配置之后，没有写入历史记录，原因是his_config_info表中id字段没有自增，没有默认值，但nacos程序中没有正确传入id字段导致编辑后保存操作报错。
2. 登录接口没有对Authorization参数放行，导致报错：令牌不能为空。
3. 
