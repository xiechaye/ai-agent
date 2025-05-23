package cn.edu.xmu.aiagent.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 工具集中注册
 *
 * @author Suave
 */
@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Value("${gaode.api-key}")
    private String gaodeApiKey;

    @Bean
    public ToolCallback[] allTools() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        WebScrapeTool webScrapeTool = new WebScrapeTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        TerminateTool terminateTool = new TerminateTool();
        QueryWeatherTool queryWeatherTool = new QueryWeatherTool(gaodeApiKey);
        CityCodeQueryTool cityCodeQueryTool = new CityCodeQueryTool();
        return ToolCallbacks.from(
                cityCodeQueryTool,
                queryWeatherTool,
                fileOperationTool,
                webSearchTool,
                terminateTool,
                webScrapeTool,
                resourceDownloadTool,
                terminalOperationTool,
                pdfGenerationTool
        );
    }
}

