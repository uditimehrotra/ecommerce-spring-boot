package com.ecommerce.platform.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GenAIService {

    private final ChatClient chatClient;

    public GenAIService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String generateDescription(String productName) {
        String prompt = "You are a marketing expert for an e-commerce store. " +
                        "Write a 2-sentence catchy and professional description for a product named: " + productName;
        
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}