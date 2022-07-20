#### 1. 启动本地CAT
cd docker2的副本
docker-compose up



//各个service信息
* [acme-financial-ui](acme-financial-ui):9081
* [acme-financial-back-office-microservice](acme-financial-back-office-microservice):9082
* [acme-financial-account-microservice](acme-financial-account-microservice):9083
* [acme-financial-customer-microservice](acme-financial-customer-microservice):9084



#### 3. 调用API

通过postman分别调用如下API：

```
http://localhost:9081/start
```

#### 4. 通过CAT查看调用链
                                                            ->acme-financial-account-microservice
acme-financial-ui ->acme-financial-back-office-microservice
                                                            ->acme-financial-customer-microservice

