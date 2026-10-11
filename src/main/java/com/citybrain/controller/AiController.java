package com.citybrain.controller;

import com.citybrain.dto.AiChatRequest;
import com.citybrain.dto.AiChatResponse;
import com.citybrain.service.AiRateLimiterService;
import com.citybrain.service.CityBrainContextService;
import com.citybrain.service.GeminiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final GeminiService geminiService;
    private final CityBrainContextService contextService;
    private final AiRateLimiterService rateLimiterService;

    public AiController(
            GeminiService geminiService,
            CityBrainContextService contextService,
            AiRateLimiterService rateLimiterService
    ) {
        this.geminiService = geminiService;
        this.contextService = contextService;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            @Valid @RequestBody AiChatRequest request,
            Authentication authentication
    ) {
        String username = (authentication != null && authentication.getName() != null)
                ? authentication.getName()
                : "anonymous";

        // Check user rate limits (15 requests/min)
        rateLimiterService.checkRateLimit(username);

        // Fetch targeted City Brain context from MySQL
        String dbContext = contextService.buildContext(request.getMessage());

        // Call Gemini / Generative reasoning engine
        String aiResponse = geminiService.generateResponse(request.getMessage(), dbContext);

        return ResponseEntity.ok(new AiChatResponse(aiResponse));
    }
}
