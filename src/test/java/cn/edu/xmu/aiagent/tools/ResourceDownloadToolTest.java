package cn.edu.xmu.aiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
class ResourceDownloadToolTest {

    @Test
    void downloadResource() {
        String url = "https://cdn.deepseek.com/logo.png?x-image-process=image%2Fresize%2Cw_1920";
        String fileName = "logo.png";
        String result = new ResourceDownloadTool().downloadResource(url, fileName);
        Assertions.assertNotNull(result);
    }
}
