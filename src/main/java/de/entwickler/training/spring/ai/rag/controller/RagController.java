package de.entwickler.training.spring.ai.rag.controller;

import de.entwickler.training.spring.ai.rag.model.QueryHistory;
import de.entwickler.training.spring.ai.rag.repository.QueryHistoryRepository;
import de.entwickler.training.spring.ai.rag.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
@Validated
public class RagController {

    private final RagService ragService;
    private final QueryHistoryRepository queryHistoryRepository;

    @Autowired
    public RagController(RagService ragService, QueryHistoryRepository queryHistoryRepository) {
        this.ragService = ragService;
        this.queryHistoryRepository = queryHistoryRepository;
    }

    @GetMapping("/history")
    public ResponseEntity<List<QueryHistory>> getQueryHistory() {
        return ResponseEntity.ok(queryHistoryRepository.findMostRecentQueries(50));
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> getAutocompleteSuggestions() {
        return ResponseEntity.ok(queryHistoryRepository.findDistinctQueryTexts(20));
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

        // Save query to history
        QueryHistory queryHistory = new QueryHistory(question);
        queryHistoryRepository.save(queryHistory);

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
