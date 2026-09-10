package com.example.ragdemo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    // ChatClient.Builder and VectorStore are auto-configured by Spring AI
    // starters based on application.properties — no manual wiring needed.
    public RagService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Step 1: Ingestion.
     * Reads a text resource, splits it into chunks, and stores
     * the chunks (as embeddings) in the vector store (Postgres/pgvector).
     */
    public void ingest(Resource resource) {
        TextReader textReader = new TextReader(resource);
        List<Document> documents = textReader.get();

        // Splits long documents into smaller chunks so retrieval is precise
        // and each chunk fits comfortably inside the LLM's context.
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(documents);

        // Spring AI handles calling the embedding model AND writing to
        // pgvector in one call — this is the boilerplate the framework saves you.
        vectorStore.add(chunks);
    }

    /**
     * Step 2: Retrieval + Generation.
     * Finds the most relevant chunks for the question, then asks
     * the LLM to answer using only that retrieved context.
     */
    public String ask(String question) {
        // Retrieval: similarity search against the vector store
        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder().query(question).topK(4).build()
        );

        String context = relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

        // Generation: pass retrieved context + question to Claude
        String promptTemplate = """
                You are a helpful assistant. Answer the question using ONLY
                the context below. If the answer isn't in the context, say
                you don't know.

                Context:
                %s

                Question:
                %s
                """;

        String prompt = promptTemplate.formatted(context, question);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
