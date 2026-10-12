package com.chat.app.controller;

import com.chat.app.model.ChatMessage;
import com.chat.app.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles the chat page and the real-time (WebSocket/STOMP) messages.
 * It only receives input and passes it to the service.
 */
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;

    // Browser sends to /app/rooms/{roomId}/sendMessage; everyone subscribed to
    // /topic/rooms/{roomId}/messages receives the saved message.
    @MessageMapping("/rooms/{roomId}/sendMessage")
    @SendTo("/topic/rooms/{roomId}/messages")
    public ChatMessage sendRoomMessage(@DestinationVariable Long roomId, ChatMessage message) {
        return chatService.saveMessage(roomId, message);
    }

    // "is typing..." signals are broadcast to the room but never saved
    @MessageMapping("/rooms/{roomId}/typing")
    @SendTo("/topic/rooms/{roomId}/typing")
    public ChatMessage roomTyping(@DestinationVariable Long roomId, ChatMessage message) {
        message.setType("TYPING");
        return message;
    }

    @MessageMapping("/rooms/{roomId}/stopTyping")
    @SendTo("/topic/rooms/{roomId}/typing")
    public ChatMessage roomStopTyping(@DestinationVariable Long roomId, ChatMessage message) {
        message.setType("STOP_TYPING");
        return message;
    }

    // GET /chat returns the Thymeleaf template templates/chat.html
    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }
}