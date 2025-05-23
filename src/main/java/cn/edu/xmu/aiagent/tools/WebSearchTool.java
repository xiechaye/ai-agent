package cn.edu.xmu.aiagent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 联网搜索工具类
 *
 * @author Suave
 */
public class WebSearchTool {

    // SearchAPI 的搜索接口地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private final String apiKey;

    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    @Tool(description = "Search for information from Bing Search Engine")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query,
            @ToolParam(description = "Optional location parameter (e.g. '(Fujian,China)'") String location) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", "bing");

        if (location != null && !location.isEmpty()) {
            paramMap.put("location", location);
        }

        try {
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);
            System.out.println("API Response: " + response);

            JSONObject jsonObject = JSONUtil.parseObj(response);

            if (jsonObject.containsKey("error")) {
                return "API Error: " + jsonObject.getStr("error");
            }

            StringBuilder results = new StringBuilder("搜索结果:\n\n");

            if (jsonObject.containsKey("organic_results")) {
                JSONArray organicResults = jsonObject.getJSONArray("organic_results");
                int count = Math.min(organicResults.size(), 5);

                for (int i = 0; i < count; i++) {
                    JSONObject item = organicResults.getJSONObject(i);
                    results.append(i + 1).append(". ").append(item.getStr("title", "无标题")).append("\n")
                            .append("   来源: ").append(item.getStr("source", "未知来源")).append("\n")
                            .append("   链接: ").append(item.getStr("link", "无链接")).append("\n")
                            .append("   摘要: ").append(item.getStr("snippet", "无摘要")).append("\n\n");
                }

                return results.toString();
            } else {
                return "未找到搜索结果";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "搜索错误: " + e.getMessage();
        }
    }

}
