# 该镜像需要依赖的基础镜像
FROM java:8
# 将当前目录下的jar包复制到docker容器的/目录下
RUN bash -c 'mkdir -p /data/appdatas/cat'
#ADD wx-fund-0.0.1-SNAPSHOT.jar /wx-fund-0.0.1-SNAPSHOT.jar
ADD client.xml /data/appdatas/cat/client.xml
# 运行过程中创建一个my_test.jar文件
#RUN bash -c 'touch /my_test.jar.jar'
# 声明服务运行在9090端口
EXPOSE 8083
EXPOSE 8082
EXPOSE 8084
EXPOSE 8085
# 指定docker容器启动时运行jar包
ENTRYPOINT ["java", "-jar","/acme-financial-ui.jar"]
ENTRYPOINT ["java", "-jar","/acme-financial-customer.jar"]
ENTRYPOINT ["java", "-jar","/acme-financial-back-office.jar"]
ENTRYPOINT ["java", "-jar","/acme-financial-account.jar"]
# 指定维护者的名字
MAINTAINER xiaoguaishou