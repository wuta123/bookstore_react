# 使用基础镜像，比如 OpenJDK
FROM openjdk:19

# 设置工作目录
WORKDIR /app

# 将 Spring Boot JAR 文件复制到容器中的工作目录
COPY target/bookstore-backend-0.0.1-SNAPSHOT.jar app.jar

# 暴露应用程序的端口（如果有必要）
EXPOSE 8080

# 设置启动命令，运行 Spring Boot 应用程序
CMD ["java", "-jar", "app.jar"]
