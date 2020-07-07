# 单向认证
[ssl证书格式的区别](https://blog.freessl.cn/ssl-cert-format-introduce/)
[https配置教程](https://www.thomasvitale.com/https-spring-boot-ssl-certificate/)

### 摘要

1. 使用keytool生成证书
  ``` 
    keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 -keystore keystore.jks -validity 3650 -storepass password 
  ```

2. 配置spring boot
  ``` 
    server.ssl.key-store-type=PKCS12
    server.ssl.key-store=classpath:keystore.p12
    server.ssl.key-store-password=password
    server.ssl.key-alias=tomcat 
  ```

# 双向认证

[双向认证](https://github.com/codependent/spring-boot-ssl-mutual-authentication)
