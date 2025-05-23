package cn.edu.xmu.aiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * 网页抓取工具类
 *
 * @author Suave
 */
public class WebScrapeTool {

    @Tool(description = "Scrape content from a webpage")
    public String scrapeWeb(@ToolParam(description = "URL of webpage to scrape") String url) {
        try {
            // 设置用户代理和超时时间，避免被网站屏蔽
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
                    .timeout(10000)
                    .get();

            // 提取页面关键信息
            StringBuilder result = new StringBuilder();

            // 1. 标题
            String title = document.title();
            result.append("标题: ").append(title).append("\n\n");

            // 2. 元描述（如果有）
            Elements metaDescription = document.select("meta[name=description]");
            if (!metaDescription.isEmpty()) {
                String description = metaDescription.attr("content");
                if (!description.isEmpty()) {
                    result.append("描述: ").append(description).append("\n\n");
                }
            }

            // 3. 页面正文内容 - 尝试几种常见的内容容器选择器
            String mainContent = "";
            // 尝试找到主要内容区域（按常见布局标签尝试）
            Elements contentElements = document.select("article, .content, .article, .post, main, #content, #main");
            if (!contentElements.isEmpty()) {
                // 使用第一个匹配的内容区域
                mainContent = contentElements.first().text();
            } else {
                // 如果找不到特定内容区域，则使用body，但限制长度
                mainContent = document.body().text();
                // 如果内容太长，截断它
                if (mainContent.length() > 5000) {
                    mainContent = mainContent.substring(0, 5000) + "...(内容已截断)";
                }
            }
            result.append("主要内容:\n").append(mainContent).append("\n\n");

            // 4. 提取重要链接（限制数量）
            Elements links = document.select("a[href]");
            if (!links.isEmpty()) {
                result.append("重要链接:\n");
                int linkCount = 0;
                for (Element link : links) {
                    String linkText = link.text().trim();
                    String linkHref = link.attr("abs:href"); // 获取绝对URL

                    // 过滤掉空链接和太短的链接文本
                    if (!linkHref.isEmpty() && linkText.length() > 3 && !linkHref.contains("javascript:")) {
                        result.append("- ").append(linkText).append(": ").append(linkHref).append("\n");
                        linkCount++;

                        // 限制链接数量
                        if (linkCount >= 10) {
                            break;
                        }
                    }
                }
            }

            return result.toString();
        } catch (IOException e) {
            return "抓取网页出错: " + e.getMessage();
        }
    }
}
