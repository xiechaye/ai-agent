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

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，联网帮我搜索一下，推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近和对象吵架了，看看恋爱网站（https://github.com/xiechaye）的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(chatId, message);
        Assertions.assertNotNull(answer);
    }

    // 测试mcp服务调用
    @Test
    void doChatWithToolsAndMcp() {
        String uuid = UUID.randomUUID().toString();
        String message = "我的另一半在厦门大学翔安校区，请帮我找到5公里范围内适合约会的地点。";
        String content = loveApp.doChatWithToolsAndMcp(uuid, message);
        Assertions.assertNotNull(content);
    }
}