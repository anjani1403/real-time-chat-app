package com.chat.app.service;

import com.chat.app.model.ChatMessage;
import com.chat.app.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        message.setId(null);
        message.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getRecentMessages() {
        List<ChatMessage> messages = new ArrayList<>(chatMessageRepository.findTop50ByOrderByIdDesc());
        Collections.reverse(messages);
        return messages;
    }
}