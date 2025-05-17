package cn.edu.xmu.aiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebSearchToolTest {

    @Value("${search-api.api-key}")
    private String apiKey;
    @Test
    void searchWeb() {
        String query = "恋爱攻略";
        String content = new WebSearchTool(apiKey).searchWeb(query);
        Assertions.assertNotNull(content);
    }
}