package cn.edu.xmu.aiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
            Document document = Jsoup.connect(url).get();
            return document.html();
        } catch (IOException e) {
            return "Error Scrape webpage" + e.getMessage();
        }
    }
}
