package cn.edu.xmu.aiagent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")  // 这里应该是路径模式，不是URL
                // 允许发送 Cookie
                .allowCredentials(true)
                // 允许特定域名访问，这是你的前端地址
                .allowedOriginPatterns("http://120.26.19.193", "http://localhost:5173") // 或者使用 allowedOriginPatterns
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}
