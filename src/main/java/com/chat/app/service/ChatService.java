package com.chat.app.service;

import com.chat.app.model.ChatMessage;
import com.chat.app.model.Room;
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
    private final RoomService roomService;

    // Saves a message into the given room
    public ChatMessage saveMessage(Long roomId, ChatMessage message) {
        Room room = roomService.getRoom(roomId);   // 404 if the room doesn't exist
        message.setId(null);                       // always a new row, never an update
        message.setRoom(room);                     // the server decides the room
        message.setTimestamp(LocalDateTime.now()); // the server decides the time
        return chatMessageRepository.save(message);
    }

    // Last 50 messages of a room, oldest first
    public List<ChatMessage> getRecentMessages(Long roomId) {
        roomService.getRoom(roomId);               // 404 if the room doesn't exist
        List<ChatMessage> messages = new ArrayList<>(chatMessageRepository.findTop50ByRoomIdOrderByIdDesc(roomId));
        Collections.reverse(messages);
        return messages;
    }
}