package com.ecommerce.platform.controller;

import com.ecommerce.platform.service.GenAIService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final GenAIService genAIService;

    public AIController(GenAIService genAIService) {
        this.genAIService = genAIService;
    }

    @GetMapping("/generate")
    public String generate(@RequestParam String productName) {
        return genAIService.generateDescription(productName);
    }
}