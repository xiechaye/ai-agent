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

    @Test
    void doChat() {
    }

    @Test
    void doChatWithReport() {
    }

    // 应用RAG知识库问答
    @Test
    void doChatWithRag() {
        String uuid = UUID.randomUUID().toString();
        String message = "我是茶叶，要怎么样才能找到对象";
        String s = loveApp.doChatWithRag(uuid, message);
        Assertions.assertNotNull(s);
    }

    // 应用RAG检索增强服务
    @Test
    void doChatWithCloudRag() {
        String uuid = UUID.randomUUID().toString();
        String message = "我是茶叶，要怎么样才能找到对象";
        String s = loveApp.doChatWithRag(uuid, message);
        Assertions.assertNotNull(s);
    }
}