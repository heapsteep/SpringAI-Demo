package com.heapsteep.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    /*@Autowired
    private ChatMemory chatMemory;*/

    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel)
                //.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * Local ONNX embedding model. Runs entirely in-process via Deep Java Library /
     * ONNX Runtime - no API key, no network calls. Default model is
     * sentence-transformers/all-MiniLM-L6-v2 which produces 384-dim vectors,
     * matching `spring.ai.vectorstore.pgvector.dimensions=384`.
     *
     * This is what makes RAG work even though Groq has no embeddings endpoint.
     */
    @Bean
    public EmbeddingModel embeddingModel() throws Exception {
        TransformersEmbeddingModel model = new TransformersEmbeddingModel();
        model.afterPropertiesSet();
        return model;
    }
}
