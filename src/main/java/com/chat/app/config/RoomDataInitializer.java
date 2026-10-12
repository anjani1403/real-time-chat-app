package com.chat.app.config;

import com.chat.app.model.Room;
import com.chat.app.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoomDataInitializer implements CommandLineRunner {

    private final RoomRepository roomRepository;

    @Override
    public void run(String... args) {
        List<String> defaultRooms = List.of("General", "Java Help", "Random");
        for (String name : defaultRooms) {
            if (!roomRepository.existsByName(name)) {
                roomRepository.save(new Room(name));
            }
        }
    }
}