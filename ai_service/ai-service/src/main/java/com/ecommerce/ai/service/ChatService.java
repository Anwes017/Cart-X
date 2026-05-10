package com.ecommerce.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public String ask(String question) {

        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(5)
                .build();

        List<Document> docs = vectorStore.similaritySearch(request);

        System.out.println("🔍 Retrieved docs = " + docs.size());
        docs.forEach(d -> System.out.println("📄 DOC:\n" + d.getText()));

        if (docs.isEmpty()) {
            return "I don't have product info for that yet. Please create the product first.";
        }

        String context = docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        String prompt = """
SYSTEM RULES (MANDATORY):
- You are NOT allowed to use outside knowledge.
- You MUST answer ONLY from PRODUCT INFO.
- If PRODUCT INFO does not contain the answer, reply EXACTLY:
  "Not available in product data"

====================
PRODUCT INFO:
%s
====================

USER QUESTION:
%s

FINAL ANSWER:
""".formatted(context, question);

        return chatClient.prompt(prompt).call().content();
    }
}