package cn.edu.xmu.aiagent.controller;

import cn.edu.xmu.aiagent.agent.LoveManus;
import cn.edu.xmu.aiagent.app.LoveApp;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
@AllArgsConstructor
public class AiController {
    private final LoveApp loveApp;
    private final ToolCallback[] allTools;
    private final ChatModel dashscopeChatModel;

    /**
     * 与LoveApp进行聊天
     * @param chatId 会话id
     * @param message 消息
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSse(String chatId, String message) {
        return loveApp.doChat(chatId, message);
    }

    /**
     * 与LoveApp进行聊天（使用基于Rag知识库）
     * @param chatId 会话id
     * @param message 消息
     * @return
     */
    @GetMapping(value = "/love_app/chat/rag/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppWithRagSse(String chatId, String message) {
        return loveApp.doChatWithRag(chatId, message);
    }

    /**
     * 与LoveApp进行聊天（使用基于工具和mcp服务）
     * @param chatId 会话id
     * @param message 消息
     * @return
     */
    @GetMapping(value = "/love_app/chat/tool/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppWithToolsAndMcpSse(String chatId, String message) {
        return loveApp.doChatWithToolsAndMcp(chatId, message);
    }


    /**
     * 与LoveManus进行聊天
     * @param message 消息
     * @return
     */
    @GetMapping("/love_manus/chat/sse")
    public SseEmitter doChatWithLoveManusSse(String message) {
        LoveManus loveManus = new LoveManus(allTools, dashscopeChatModel);
        return loveManus.run(message);
    }
}
