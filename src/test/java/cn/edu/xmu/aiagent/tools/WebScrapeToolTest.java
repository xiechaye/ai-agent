package cn.edu.xmu.aiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebScrapeToolTest {

    @Test
    void scrapeWeb() {
        String url = "https://github.com/xiechaye";
        String html = new WebScrapeTool().scrapeWeb(url);
        Assertions.assertNotNull(html);
    }
}