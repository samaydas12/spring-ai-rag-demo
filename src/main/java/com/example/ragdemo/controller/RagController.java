package com.example.ragdemo.controller;

import com.example.ragdemo.service.RagService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    /**
     * Upload plain text to be chunked, embedded, and stored.
     * Example: POST /api/rag/ingest  body: { "text": "Employees get 12 casual leaves per year..." }
     */
    @PostMapping("/ingest")
    public Map<String, String> ingest(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        Resource resource = new ByteArrayResource(text.getBytes(StandardCharsets.UTF_8));
        ragService.ingest(resource);
        return Map.of("status", "ingested");
    }

    /**
     * Ask a question — retrieves relevant chunks and generates an answer.
     * Example: GET /api/rag/ask?question=How many casual leaves do I get?
     */
    @GetMapping("/ask")
    public Map<String, String> ask(@RequestParam String question) {
        String answer = ragService.ask(question);
        return Map.of("question", question, "answer", answer);
    }
}
