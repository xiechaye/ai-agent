package cn.edu.xmu.aiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppTest {
    @Resource
    private LoveApp loveApp;

    @Test
    void testDoChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "我是茶叶，我的对象是Suave，请让他更爱我";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(chatId, message);
        Assertions.assertNotNull(loveReport);
    }
}