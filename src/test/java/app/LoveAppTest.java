package app;

import cn.edu.xmu.aiagent.AiAgentApplication;
import cn.edu.xmu.aiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AiAgentApplication.class)
class LoveAppTest {

    @Resource
    private LoveApp loveApp;


    @Test
    void testDoChat() {
        loveApp.doChat("test", "你好，我是Suave");
    }
}