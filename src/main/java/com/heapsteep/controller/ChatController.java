package com.heapsteep.controller;

import com.heapsteep.model.ChatRequest;
import com.heapsteep.service.ChatService;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    ChatMemory chatMemory;

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
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
}
