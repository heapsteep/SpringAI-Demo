package com.heapsteep.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ImageCaptionService {

    private final ChatClient chatClient;

    @Value("${spring.ai.vision.model:meta-llama/llama-4-scout-17b-16e-instruct}")
    private String visionModel;

    public ImageCaptionService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String caption(MultipartFile file, String message) throws IOException {
        
        var imageResource = new ByteArrayResource(file.getBytes());

        Prompt prompt = new Prompt(List.of(
                        new SystemMessage("You are an expert image analyst. Describe images clearly and accurately."),
                        UserMessage.builder()
                                    .text(message)
                                    .media(Media.builder().mimeType(MimeTypeUtils.IMAGE_JPEG).data(imageResource).build())
                                    .build()
                        ),
                        OpenAiChatOptions.builder().model(visionModel).build()
        );

        return chatClient.prompt(prompt)
                .call()
                .content();
    }
}
