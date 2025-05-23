package cn.edu.xmu.aiagent.tools;

import cn.hutool.core.date.DateUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 日期时间工具类
 *
 * @author AI Assistant
 */
public class QueryDateTimeTool {
    
    /**
     * 获取当前日期（仅年月日）
     * @return 当前日期，格式为 yyyy-MM-dd
     */
    @Tool(description = "Get current date (year-month-day only), returns in yyyy-MM-dd format", returnDirect = true)
    public String getCurrentDate() {
        return DateUtil.today();
    }
    
    /**
     * 获取当前星期几
     * @return 当前星期几，中文格式
     */
    @Tool(description = "Get the current day of the week, returns in Chinese format such as: 星期一 (Monday), 星期二 (Tuesday), etc.", returnDirect = true)
    public String getCurrentDayOfWeek() {
        return DateUtil.dayOfWeekEnum(DateUtil.date()).toChinese("星期");
    }
} 