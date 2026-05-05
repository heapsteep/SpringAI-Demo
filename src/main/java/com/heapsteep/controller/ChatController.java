package com.heapsteep.controller;

import com.heapsteep.model.ChatRequest;
import com.heapsteep.service.ChatService;
import com.heapsteep.service.TravelPlanService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatService chatService;
    private final TravelPlanService travelPlanService;

    public ChatController(ChatService chatService, TravelPlanService travelPlanService) {
        this.chatService = chatService;
        this.travelPlanService = travelPlanService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest chatRequest){
        return chatService.chat(chatRequest.getMessage());

    }
}
