package cn.edu.xmu.aiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@SpringBootTest
class LoveAppTest {
    @Resource
    private LoveApp loveApp;
    @Resource
    private VectorStore pgVectorStore;

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

    // 应用RAG问答知识库（基于pgVector）
    @Test
    void doChatWithRagPgVector() {
        List<Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));
        pgVectorStore.add(documents);
        String uuid = UUID.randomUUID().toString();
        String message = "我是茶叶，要怎么样才能找到对象";
        String s = loveApp.doChatWithRag(uuid, message);
        Assertions.assertNotNull(s);
    }
}