package com.heapsteep.controller;

import com.heapsteep.model.ChatRequest;
import com.heapsteep.service.ChatService;
import com.heapsteep.service.RagIngestionService;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    ChatMemory chatMemory;

    private final ChatService chatService;
    private final RagIngestionService ragIngestionService;
    private final VectorStore vectorStore;

    public ChatController(ChatService chatService,
                          RagIngestionService ragIngestionService,
                          VectorStore vectorStore) {
        this.chatService = chatService;
        this.ragIngestionService = ragIngestionService;
        this.vectorStore = vectorStore;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest chatRequest){
        return chatService.chat(chatRequest.getConversationId(), chatRequest.getMessage());
    }

    @GetMapping("/fetchMemory")
    public List<Message> fetchMemory(){
        return chatMemory.get("default");
    }

    @GetMapping("/fetchMemoryWithId")
    public List<Message> fetchMemoryWithId(@RequestParam String conversationId){
        return chatMemory.get(conversationId);
    }

    /**
     * Demo endpoint: upload an additional PDF at runtime and embed it into pgvector.
     */
    @PostMapping(value = "/rag/upload")
    public Map<String, Object> uploadPdf(@RequestParam("file") MultipartFile file) throws Exception {
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        ragIngestionService.ingestPdf(resource);
        return Map.of("status", "ok", "file", file.getOriginalFilename());
    }

    /**
     * Demo endpoint: see what the retriever returns for a query (handy to verify RAG is working).
     */
    @GetMapping("/rag/search")
    public List<Document> search(@RequestParam String q,
                                 @RequestParam(defaultValue = "4") int topK) {
        return vectorStore.similaritySearch(
                SearchRequest.builder().query(q).topK(topK).build()
        );
    }
}
