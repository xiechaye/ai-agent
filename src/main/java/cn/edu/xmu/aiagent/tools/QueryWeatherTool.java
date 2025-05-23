package cn.edu.xmu.aiagent.tools;

import cn.hutool.http.HttpUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.Map;

/**
 * 查询天气工具
 *
 * @author Suave
 */
public class QueryWeatherTool {

    private final String API_URL = "https://restapi.amap.com/v3/weather/weatherInfo?";

    private String apiKey;

    public QueryWeatherTool(String apiKey) {
        this.apiKey = apiKey;
    }

    @Tool(description = "Query weather information for specified Chinese cities. Provides current weather conditions, temperature, humidity, wind power, and other meteorological data. Suitable for scenarios requiring weather information for specific cities, such as travel planning, activity arrangements, etc. Returns weather data in JSON format.", returnDirect = true)
    public String queryWeather(@ToolParam(description = "City code for Chinese cities, used to identify the city for which weather information is needed. Please use standard city codes, such as '110000' for Beijing, '310000' for Shanghai, etc. If the code is uncertain, you can provide the city name, and the system will attempt to match the closest city.") String city,
                               @ToolParam(description = "Type of weather information to return. Options: 'base' returns only current weather conditions; 'all' returns forecast weather information for upcoming days. Default is 'base'.") String extensions) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("key", apiKey);
        paramMap.put("city", city);
        paramMap.put("extensions", extensions);
        paramMap.put("format", "json");

        return HttpUtil.get(API_URL, paramMap);
    }
}
