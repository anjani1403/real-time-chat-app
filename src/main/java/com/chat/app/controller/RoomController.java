package com.chat.app.controller;

import com.chat.app.model.Room;
import com.chat.app.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/api/rooms")
    public List<Room> getRooms() {
        return roomService.getAllRooms();
    }
}