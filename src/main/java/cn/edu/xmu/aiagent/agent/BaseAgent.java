package cn.edu.xmu.aiagent.agent;

import cn.edu.xmu.aiagent.agent.model.AgentState;
import cn.edu.xmu.aiagent.common.ErrorCode;
import cn.edu.xmu.aiagent.exception.BusinessException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Data
public abstract class BaseAgent {
    // 核心属性
    private String name;

    // 提示词
    private String systemPrompt;
    private String nextStepPrompt;

    // 状态(默认为空闲)
    private AgentState agentState = AgentState.IDLE;

    // 执行控制
    private int currentStep = 0;
    private int maxStep = 10;

    // LLM
    private ChatClient chatClient;

    // Memory(自主维护上下文)
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行agent
     * @return
     */
    public SseEmitter run(String userPrompt) {
        SseEmitter sseEmitter = new SseEmitter(300000L);

        // 使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                // 检查agent是否空闲
                if(agentState != AgentState.IDLE) {
                    sseEmitter.send("错误：无法从状态运行代理：" + agentState);
                    sseEmitter.complete();
                    return;
                }
                // 检查用户输入是否为空
                if(StringUtils.isBlank(userPrompt)) {
                    sseEmitter.send("错误：不能使用空提示词运行代理。");
                    sseEmitter.complete();
                    return;
                }

                // 将用户消息添加到上下文
                messageList.add(new UserMessage(userPrompt));

                // 保存结果列表
                List<String> resultList = new ArrayList<>();

                // 设置状态为运行中
                agentState = AgentState.RUNNING;

                try {
                    while(currentStep < maxStep && agentState != AgentState.FINISHED) {
                        currentStep += 1;
                        log.info("Executing step:" + currentStep + "/" + maxStep);
                        String result = step();
                        log.info("Step result:" + result);
                        resultList.add(result);
                        // 每执行一步发送一次
                        sseEmitter.send(result);
                    }

                    // 运行步骤过多
                    if(currentStep >= maxStep) {
                        agentState = AgentState.FINISHED;
                        resultList.add("Terminated: Reached max steps (" + maxStep + ")");
                        sseEmitter.send("执行结束：达到最大步骤 (" + maxStep + ")");
                    }

                    sseEmitter.complete();
                } catch (Exception e) {
                    agentState = AgentState.ERROR;
                    log.error("Error during agent execution", e);
                    sseEmitter.completeWithError(e);
                } finally {
                    cleanup();
                }
            } catch (IOException e) {
                sseEmitter.completeWithError(e);
            }
        });

        sseEmitter.onTimeout(() -> {
            this.agentState = AgentState.ERROR;
            this.cleanup();
            log.error("Sse connection timeout");
        });

        sseEmitter.onCompletion(() -> {
            if(this.agentState == AgentState.RUNNING) {
                this.agentState = AgentState.FINISHED;
            }

            this.cleanup();
            sseEmitter.complete();
            log.info("Sse connection completed");
        });

        return sseEmitter;
    }

    /**
     * 执行单个步骤
     * @return
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup() {
        this.systemPrompt = "";
        this.nextStepPrompt = "";
        this.messageList = null;
    }
}
