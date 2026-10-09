package com.chat.app.controller;

import com.chat.app.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.chat.app.service.ChatService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;

    //  /app/sendMessage
    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message){
        return chatService.saveMessage(message);
    }

    @MessageMapping("/typing")
    @SendTo("/topic/typing")
    public ChatMessage typing(ChatMessage message) {
        message.setType("TYPING");
        return message;
    }

    @MessageMapping("/stopTyping")
    @SendTo("/topic/typing")
    public ChatMessage stopTyping(ChatMessage message) {
        message.setType("STOP_TYPING");
        return message;
    }

    @GetMapping("/chat")
    public String chat(){
        return "chat";
    }
}
