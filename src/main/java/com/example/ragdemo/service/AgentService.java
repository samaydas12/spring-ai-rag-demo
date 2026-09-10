package com.example.ragdemo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

    private final ChatClient agentChatClient;

    // This is the ONLY difference from RagService's chat client:
    // .defaultTools(...) registers the tool methods so the LLM can
    // decide on its own, per request, whether to call them.
    public AgentService(ChatClient.Builder chatClientBuilder, LeaveTools leaveTools) {
        this.agentChatClient = chatClientBuilder
                .defaultTools(leaveTools)
                .build();
    }

    public String ask(String question) {
        return agentChatClient.prompt()
                .user(question)
                .call()
                .content();
    }
}
