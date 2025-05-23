package cn.edu.xmu.aiagent.tools;

import cn.edu.xmu.aiagent.constant.FileConstant;
import com.google.common.reflect.ClassPath;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 城市编码查询工具
 * 
 * @author Suave
 */
@Component
public class CityCodeQueryTool {

    private static final String CITY_CODE_FILE = FileConstant.FILE_SAVE_DIR +  "/file/AMap_adcode_citycode.xlsx";
    
    // 缓存城市名称到adcode的映射
    private final Map<String, String> cityToAdcodeMap = new HashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        loadCityCodesFromExcel();
    }
    
    /**
     * 从Excel文件加载城市编码数据
     */
    private void loadCityCodesFromExcel() {
        try (FileInputStream fis = new FileInputStream(new File(CITY_CODE_FILE));
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0); // 假设数据在第一个工作表
            
            // 跳过表头，从第二行开始读取数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    // 读取中文名和adcode
                    Cell nameCell = row.getCell(0); // 中文名
                    Cell adcodeCell = row.getCell(1); // adcode
                    
                    if (nameCell != null && adcodeCell != null) {
                        String cityName = getCellValueAsString(nameCell);
                        String adcode = getCellValueAsString(adcodeCell);
                        
                        if (!cityName.isEmpty() && !adcode.isEmpty()) {
                            cityToAdcodeMap.put(cityName, adcode);
                        }
                    }
                }
            }
            
            System.out.println("城市编码数据加载完成，共 " + cityToAdcodeMap.size() + " 条记录");
            
        } catch (Exception e) {
            System.err.println("加载城市编码数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 获取单元格的值并转换为字符串
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                } else {
                    // 处理数值为整数的情况
                    double value = cell.getNumericCellValue();
                    if (value == Math.floor(value)) {
                        return String.valueOf((long) value);
                    }
                    return String.valueOf(value);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception ex) {
                        return "";
                    }
                }
            default:
                return "";
        }
    }

    @Tool(description = "Query the area code (adcode) for Chinese cities. The adcode is used for weather queries and other location-based services. This tool helps translate city names to their corresponding adcodes in the Chinese administrative division system.")
    public String queryCityCode(@ToolParam(description = "The Chinese city name to look up. Can be a province, city, or district name, such as '北京', '上海', '广州', etc. The system will return the most relevant adcode if found.") String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            return "Error: City name cannot be empty.";
        }
        
        // 尝试精确匹配
        String adcode = cityToAdcodeMap.get(cityName.trim());
        
        // 如果精确匹配失败，尝试模糊匹配
        if (adcode == null) {
            for (Map.Entry<String, String> entry : cityToAdcodeMap.entrySet()) {
                if (entry.getKey().contains(cityName) || cityName.contains(entry.getKey())) {
                    adcode = entry.getValue();
                    break;
                }
            }
        }
        
        if (adcode != null) {
            return adcode;
        } else {
            return "City code not found for '" + cityName + "'. Please check the city name and try again.";
        }
    }
}
