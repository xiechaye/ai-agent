# AI智能体

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