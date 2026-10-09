package com.chat.app.controller;

import com.chat.app.model.ChatMessage;
import com.chat.app.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final ChatService chatService;

    @GetMapping("/api/messages")
    public List<ChatMessage> getRecentMessages() {
        return chatService.getRecentMessages();
    }
}