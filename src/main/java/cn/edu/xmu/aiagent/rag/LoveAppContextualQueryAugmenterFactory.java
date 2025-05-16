package cn.edu.xmu.aiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

public class LoveAppContextualQueryAugmenterFactory {

    public static ContextualQueryAugmenter createContextualQueryAugmenter() {
        // 查询结果为空时的默认回复
        String emptyContextPrompt = "你应该输出下面的内容：" +
                "抱歉，我只能回答恋爱相关的问题，" +
                "别的没办法帮到您哦有问题可以联系客服https://github.com/xiechaye";

        PromptTemplate promptTemplate = new PromptTemplate(emptyContextPrompt);
        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .emptyContextPromptTemplate(promptTemplate)
                .build();
    }
}
