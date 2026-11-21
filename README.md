# AI-Agent 情感顾问

## 项目介绍

AI-Agent是一个基于Java开发的智能代理系统，集成了多种实用工具类，旨在提供强大的文档处理、网络搜索和资源管理能力。本系统可以作为AI应用的后端服务，为智能助手提供丰富的交互能力和数据处理能力。

## 技术栈

- 核心框架：Spring AI Alibaba, Vue3
- PDF处理：iText 7
- HTTP工具：Hutool
- 日志处理：SLF4J、Lombok
- 云存储：阿里云OSS

## 启动项目

1. 配置工具的apikey和mcp服务的apikey
2. 使用自定义mcp服务（image-search-mcp-server模块）
    - stdio:
        1. 将image-search-mcp-server模块打包
        2. mcp-servers.json中添加
           ```json
           "image-search-mcp-server": {
              "command": "java",
              "args": [
                "-Dspring.ai.mcp.server.stdio=true",
                "-Dspring.main.web-application-type=none",
                "-Dlogging.pattern.console=",
                "-jar",
                "image-search-mcp-server/target/image-search-mcp-server-0.0.1-SNAPSHOT.jar"
              ],
              "env": {
              "api-key": "你的pexelsapi-key"
              }
            }
         ```
    - sse：
        1. 修改image-search-mcp-server模块的application.yml
           ```yml
           spring:
               application:
                  name: yu-image-search-mcp-server
               profiles:
                  active: sse
           server:
             port: 8127
           ```
        2. 客户端application.yml中添加如下配置
             ```yml
             spring:
                 ai:
                   mcp:
                     client:
                         sse:
                           connections:
                             server1:
                                 url: http://localhost:8888
             ```
        3. 客户端注释掉application.yml中的配置
            ```yml
              spring:
                 ai:
                   mcp:
                     client:
                         #stdio:
                         #  servers-configuration: classpath:mcp-servers.json
           ```
        4. 如果在mcp-servers.json中存在如下配置需删除
           ```json
           "image-search-mcp-server": {
              "command": "java",
              "args": [
                "-Dspring.ai.mcp.server.stdio=true",
                "-Dspring.main.web-application-type=none",
                "-Dlogging.pattern.console=",
                "-jar",
                "image-search-mcp-server/target/image-search-mcp-server-0.0.1-SNAPSHOT.jar"
              ],
              "env": {
              "api-key": "你的pexelsapi-key"
              }
            }
         ```
