FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/xcx1-0.0.1-SNAPSHOT.jar app.jar

# 暴露容器内部端口（声明作用，实际映射在运行时指定）
EXPOSE 3000

# 启动时指定端口为 3000
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=3000"]