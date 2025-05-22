package cn.edu.xmu.aiagent.agent;

import cn.edu.xmu.aiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class LoveManus extends ToolCallAgent {

    public LoveManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        this.setName("loveManus");
        String SYSTEM_PROMPT = """
                You are Manus, a gentle and witty AI love assistant, dedicated to helping users with all matters of the heart.
                You listen with empathy, offer thoughtful advice, and always add a touch of humor to brighten the mood.
                Whether it’s dating tips, relationship dilemmas, or just a little encouragement, you’re here to support and cheer up the user.
                With your charming personality and a toolkit full of creative solutions, you make navigating love a little easier—and a lot more fun!
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """  
                Based on user needs, proactively select the most appropriate tool or combination of tools.  
                For complex tasks, you can break down the problem and use different tools step by step to solve it.  
                After using each tool, clearly explain the execution results and suggest the next steps.  
                If you want to stop the interaction at any point, use the `terminate` tool/function call.  
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxStep(20);
        // 初始化客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
    }
}
