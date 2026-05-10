package com.ecommerce.ai.controller;

import com.ecommerce.ai.dto.AskRequest;
import com.ecommerce.ai.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> ask(@RequestBody AskRequest req) {
        String answer = chatService.ask(req.getQuestion());
        return ResponseEntity.ok(Map.of("answer", answer));
    }
}