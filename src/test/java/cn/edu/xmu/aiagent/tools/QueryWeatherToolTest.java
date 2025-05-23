package cn.edu.xmu.aiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QueryWeatherToolTest {

    @Value("${gaode.api-key}")
    private String apiKey;
    @Test
    void queryWeather() {
        QueryWeatherTool queryWeatherTool = new QueryWeatherTool(apiKey);
        String result = queryWeatherTool.queryWeather("130100", "base");
        Assertions.assertNotNull(result);
    }
}