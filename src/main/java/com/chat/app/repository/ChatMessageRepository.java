package com.chat.app.repository;

import com.chat.app.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Newest 50 messages of one room
    List<ChatMessage> findTop50ByRoomIdOrderByIdDesc(Long roomId);
}
