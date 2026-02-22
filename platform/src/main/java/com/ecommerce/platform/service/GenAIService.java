package com.ecommerce.platform.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import java.util.List;

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

    public String getSmartRecommendation(List<String> currentCartItems) {
    String items = String.join(", ", currentCartItems);
    String prompt = "The user has the following items in their shopping cart: [" + items + "]. " +
                    "Suggest one complementary product they might need and give a 1-sentence reason why. " +
                    "Be brief and professional.";
    
    return chatClient.prompt()
            .user(prompt)
            .call()
            .content();
}
}