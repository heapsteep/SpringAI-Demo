package com.heapsteep.model;

import lombok.Data;

@Data
public class ChatRequest {
    private String conversationId;
    private String message;
}
