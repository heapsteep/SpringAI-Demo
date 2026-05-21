package com.heapsteep.service;

import com.heapsteep.tools.ContactsTool;
import com.heapsteep.tools.WeatherTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final WeatherTool weatherTool;
    private final ContactsTool contactsTool;

    public ChatService(ChatClient chatClient, ChatMemory chatMemory, WeatherTool weatherTool, ContactsTool contactsTool) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.weatherTool = weatherTool;
        this.contactsTool = contactsTool;
    }

    public String chat(String conversationId, String message) {
        String convId = (conversationId == null || conversationId.isBlank()) ? UUID.randomUUID().toString() : conversationId;
        String today= LocalDate.now().toString();

        Prompt prompt = new Prompt(List.of(
                new SystemMessage("You are an experienced travel guide. Today's date is " + today),
                new UserMessage(message)
        ));

        return chatClient.prompt(prompt)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(convId).build())
                .tools(weatherTool, contactsTool)
                .call()
                .content();
    }
}