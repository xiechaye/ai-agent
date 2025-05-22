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

import java.util.ArrayList;
import java.util.List;

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
    public String run(String userPrompt) {
        // 检查agent是否空闲
        if(agentState != AgentState.IDLE) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "当前agent正在执行中");
        }
        // 检查用户输入是否为空
        if(StringUtils.isBlank(userPrompt)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户输入为空");
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
            }

            // 运行步骤过多
            if(currentStep >= maxStep) {
                agentState = AgentState.IDLE;
                resultList.add("Terminated: Reached max steps (" + maxStep + ")");
            }

            return String.join("\n", resultList);
        } catch (Exception e) {
            agentState = AgentState.ERROR;
            log.error("Error during agent execution", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        } finally {
            clearup();
        }
    }

    /**
     * 执行单个步骤
     * @return
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void clearup() {

    }
}
