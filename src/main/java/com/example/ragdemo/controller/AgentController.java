package com.example.ragdemo.controller;

import com.example.ragdemo.service.AgentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = "*")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    /**
     * Ask the agent something. Unlike /api/rag/ask, this doesn't just
     * retrieve+answer — the LLM can decide to call tools (e.g. check
     * leave balance, apply leave) as part of forming its answer.
     * Example: GET /api/agent/ask?question=How many leaves does E102 have, and apply 2 days if any left?
     */
    @GetMapping("/ask")
    public Map<String, String> ask(@RequestParam String question) {
        String answer = agentService.ask(question);
        return Map.of("question", question, "answer", answer);
    }
}
