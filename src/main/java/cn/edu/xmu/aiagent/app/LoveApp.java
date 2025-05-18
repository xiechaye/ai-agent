package cn.edu.xmu.aiagent.app;

import cn.edu.xmu.aiagent.advisor.MyLoggerAdvisor;
import cn.edu.xmu.aiagent.rag.LoveAppRagCustomAdvisorFactory;
import cn.edu.xmu.aiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;
    // 内存向量数据库
    @Resource
    private VectorStore loveAppVectorStore;
    // 云知识库
    @Resource
    private Advisor loveAppRagCloudAdvisor;
    // pgvector 向量数据库
//    @Resource
//    private VectorStore pgVectorStore;
    // 查询重写器
    @Resource
    private QueryRewriter queryRewriter;
    // 工具
    @Resource
    private ToolCallback[] allTool;
    // mcp服务
    @Resource
    private ToolCallbackProvider toolCallbackProvider;


    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatModel dashscopeChatModel) {
        // 初始化基于文件的对话记忆
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        // 基于文件的持久化存储
//        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);

//        // 初始化基于内存的对话记忆
//        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        // 默认的基于文件的持久化存储
//                        new MessageChatMemoryAdvisor(chatMemory),
                        // 自定义的日志 advisor
                        new MyLoggerAdvisor()
//                        // 自定义AI推理能力增强的 advisor
//                        new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * 与AI进行对话
     * @param chatId 会话ID
     * @param message 用户输入的消息
     * @return
     */
    public String doChat(String chatId, String message) {

        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
//        log.info(content);
        return content;
    }

    /**
     * 恋爱报告类
     */
    record LoveReport(String title, List<String> suggestions){}

    /**
     * 与AI进行对话，带有报告功能
     * @param chatId 会话ID
     * @param message 用户输入的消息
     * @return
     */
    public LoveReport doChatWithReport(String chatId, String message) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
    }

    /**
     * 与AI进行对话，应用RAG知识库问答（检索本地知识库）
     * @param chatId 会话ID
     * @param message 用户输入的消息
     * @return
     */
    public String doChatWithRag(String chatId, String message) {
        String reWrite = queryRewriter.doQueryReWrite(message);
        ChatResponse chatResponse = chatClient.prompt()
                .user(reWrite)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
//                // 应用知识库问答（内存向量数据库）
//                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
//                // 应用增强检索服务(云知识库)
//                .advisors(loveAppRagCloudAdvisor)
//                  // 应用知识库问答（pgvector 向量数据库）
//                .advisors(new QuestionAnswerAdvisor(pgVectorStore))
                // 自定义RAG检索增强服务（文档查询器+上下文增强器）
                .advisors(LoveAppRagCustomAdvisorFactory.createRagCustomAdvisor(loveAppVectorStore, "单身"))
                .call()
                .chatResponse();

        return chatResponse.getResult().getOutput().getText();
    }

    /**
     * AI调用工具
     * @param chatId
     * @param message
     * @return
     */
    public String doChatWithTools(String chatId, String message) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
//                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                .tools(allTool)
                .call()
                .chatResponse();

        return chatResponse.getResult().getOutput().getText();
    }


    /**
     * AI调用工具和MCP服务
     * @param chatId
     * @param message
     * @return
     */
    public String doChatWithToolsAndMcp(String chatId, String message) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
//                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                .tools(allTool)
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();

        return chatResponse.getResult().getOutput().getText();
    }
}
