package com.chat.app.controller;

import com.chat.app.model.ChatMessage;
import com.chat.app.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final ChatService chatService;

    // GET /api/rooms/2/messages -> recent messages of room 2
    @GetMapping("/api/rooms/{roomId}/messages")
    public List<ChatMessage> getRoomMessages(@PathVariable Long roomId) {
        return chatService.getRecentMessages(roomId);
    }
}