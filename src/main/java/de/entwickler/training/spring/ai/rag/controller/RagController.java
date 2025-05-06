package de.entwickler.training.spring.ai.rag.controller;

import de.entwickler.training.spring.ai.rag.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
@Validated
public class RagController {

    private final RagService ragService;

    @Autowired
    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/query")
    public ResponseEntity<Map<String, String>> query(@RequestBody Map<String, String> payload) {
        String question = payload.get("question");

        // Validation
        if (!StringUtils.hasText(question)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Question cannot be blank"));
        }
        if (question.length() > 400) {
            return ResponseEntity.badRequest().body(Map.of("error", "Question must be less than 400 characters"));
        }

        // Generate the prompt that will be sent
        String prompt = ragService.generatePrompt(question);

        // Generate the response
        String response = ragService.generateResponse(question, prompt);

        // Return both the prompt and the response
        Map<String, String> result = new HashMap<>();
        result.put("prompt", prompt);
        result.put("response", response);

        return ResponseEntity.ok(result);
    }
}
