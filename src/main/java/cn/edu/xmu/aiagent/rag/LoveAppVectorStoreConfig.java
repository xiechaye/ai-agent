package cn.edu.xmu.aiagent.rag;


import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 自定义的内存向量存储仓库
 *
 * @author Suave
 */
@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Bean
    public VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        // 加载文档
        List<Document> documents = loveAppDocumentLoader.loadDocuments();

        // 创建内存向量存储
        VectorStore vectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();

        // 将文档添加到向量存储
        vectorStore.add(documents);
        return vectorStore;
    }
}
