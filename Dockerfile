# 第一阶段：从node镜像获取Node.js完整文件
FROM node:18-slim AS node_source

# 预安装高德地图MCP服务
RUN npm install -g @amap/amap-maps-mcp-server

# 第二阶段：Maven构建
FROM maven:3.9-amazoncorretto-21 AS build

WORKDIR /app

# 复制源代码和pom.xml
COPY pom.xml .
COPY src ./src

# 使用Maven执行打包
RUN mvn clean package -DskipTests

# 第三阶段：最终运行时镜像
FROM openjdk:21-slim

# 复制Node.js环境
COPY --from=node_source /usr/local/bin /usr/local/bin
COPY --from=node_source /usr/local/lib /usr/local/lib
COPY --from=node_source /usr/local/include /usr/local/include
COPY --from=node_source /usr/local/share /usr/local/share

# 确保NODE_PATH设置正确
ENV NODE_PATH=/usr/local/lib/node_modules

# 工作目录
WORKDIR /app

# 创建高德地图城市编码数据目录
RUN mkdir -p /app/tmp/file

# 复制高德地图城市编码数据文件到容器
COPY AMap_adcode_citycode.xlsx /app/tmp/file/

# 从Maven构建阶段复制JAR文件
COPY --from=build /app/target/yu-ai-agent-0.0.1-SNAPSHOT.jar app.jar

# 暴露应用端口
EXPOSE 8123

# 使用生产环境配置启动应用
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]