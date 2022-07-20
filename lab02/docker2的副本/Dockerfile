# 该镜像需要依赖的基础镜像
FROM java:8
# 将当前目录下的jar包复制到docker容器的/目录下
RUN bash -c 'mkdir -p /data/appdatas/cat'
RUN bash -c 'mkdir  /work'
#ADD wx-fund-0.0.1-SNAPSHOT.jar /wx-fund-0.0.1-SNAPSHOT.jar
#ADD client.xml /data/appdatas/cat/client.xml
# 运行过程中创建一个my_test.jar文件
#RUN bash -c 'touch /my_test.jar.jar'
# 声明服务运行在9090端口
EXPOSE 9083
EXPOSE 9082
EXPOSE 9084
EXPOSE 9081
# 指定docker容器启动时运行jar包

ENTRYPOINT ["sh","/work/start.sh"]

# 指定维护者的名字
MAINTAINER xiaoguaishou