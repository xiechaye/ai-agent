package cn.edu.xmu.aiagent.tools;

import cn.edu.xmu.aiagent.constant.FileConstant;
import cn.edu.xmu.aiagent.utils.OssUtils;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

/**
 * 资源下载工具类
 *
 * @author Suave
 */
public class ResourceDownloadTool {

    @Tool(description = "Download a resource from a given URL and upload to OSS")
    public String downloadResource(@ToolParam(description = "URL of the resource to download") String url,
                                   @ToolParam(description = "Name of the file to save in OSS") String fileName) {
        try {
            // 使用临时文件方式下载
            byte[] fileBytes;

            try {
                // 使用Hutool的HttpUtil直接获取字节数组
                fileBytes = HttpUtil.downloadBytes(url);
            } catch (Exception e) {
                return "Error downloading resource: " + e.getMessage();
            }

            if (fileBytes == null || fileBytes.length == 0) {
                return "Error: Downloaded file is empty";
            }

            // 上传到OSS，生成文件名
            String objectName = System.currentTimeMillis() + "/" + fileName;

            // 调用OSS上传工具
            String ossUrl = OssUtils.uploadImage(fileBytes, objectName);

            if (ossUrl != null) {
                return "Resource downloaded and uploaded to OSS successfully: " + ossUrl;
            } else {
                return "Error uploading to OSS: OssUtils.uploadImage returned null.";
            }
        } catch (Exception e) {
            return "Error processing resource: " + e.getMessage();
        }
    }
}

