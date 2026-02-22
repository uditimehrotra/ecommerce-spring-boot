package com.ecommerce.platform.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GenAIService {

    private final ChatClient chatClient;

    // The ChatClient.Builder is automatically configured by Spring Boot
    public GenAIService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String generateDescription(String productName) {
        return chatClient.prompt()
                .user("Write a 2-sentence professional e-commerce description for: " + productName)
                .call()
                .content();
    }
}