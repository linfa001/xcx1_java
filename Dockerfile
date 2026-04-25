# Docker应用容器配置文件
# 功能：基于OpenJDK 21构建Spring Boot应用运行环境
# 主要配置：
#   - 基础镜像：openjdk:21-jdk-slim
#   - 时区设置：Asia/Shanghai
#   - 运行用户：app（系统用户）
#   - 工作目录：/app
#   - 服务端口：3001
#FROM openjdk:21-jdk-slim
FROM eclipse-temurin:21-jdk
# 设置容器时区环境变量
ENV TZ=Asia/Shanghai
# 创建系统用户组和用户，以非root用户身份运行应用，增强安全性
RUN addgroup --system app && adduser --system --group app
# 设置容器内工作目录
WORKDIR /app

# 复制构建好的Spring Boot应用到容器并重命名为app.jar
COPY target/xcx1-1.0.0.jar app.jar
# 切换到app用户，避免以root权限运行应用
USER app
# 声明容器运行时监听的端口号（文档说明，可以不写，写了方便执行命令docker ps查询）
EXPOSE 3001

# 启动时指定端口为 3001
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=3001"]