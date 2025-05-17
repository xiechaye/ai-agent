package cn.edu.xmu.aiagent.rag;

import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;


public class LoveAppRagCustomAdvisorFactory {

    // 创建一个新的 RetrievalAugmentationAdvisor 实例
    public static RetrievalAugmentationAdvisor createRagCustomAdvisor(VectorStore vectorStore, String status) {
        // 创建过滤器表达式
        Filter.Expression filterExpression = new FilterExpressionBuilder()
                .eq("status", status)
                        .build();
        // 创建向量文档检索器
        VectorStoreDocumentRetriever vectorStoreDocumentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(filterExpression)
                .build();
        // 创建一个新的 RetrievalAugmentationAdvisor 实例
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(vectorStoreDocumentRetriever)
                // 检索不到文档时处理
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createContextualQueryAugmenter())
                .build();
    }
}
