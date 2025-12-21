# 第一阶段：构建
FROM maven:3.9-eclipse-temurin-21 as builder

WORKDIR /build

# 复制 pom.xml 和源代码
COPY pom.xml .
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 第二阶段：运行
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 从构建阶段复制 JAR
COPY --from=builder /build/target/demo-0.0.1-SNAPSHOT.jar .

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]