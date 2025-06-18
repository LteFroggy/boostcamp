package com.example.backendproject.purewebsocket.room.service;

import com.example.backendproject.purewebsocket.room.entity.ChatRoom;
import com.example.backendproject.purewebsocket.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    // 룸을 만들 때는 이와 같이 일단 방이 있는지 체크하고, 없다면 만든다.
    public ChatRoom createRoom(String roomId) {
        return roomRepository.findByRoomId(roomId).orElseGet(() -> {
            ChatRoom newRoom = new ChatRoom();
            newRoom.setRoomId(roomId);
            return roomRepository.save(newRoom);
        });
    }

    public List<ChatRoom> findAllRooms() {
        return roomRepository.findAll();
    }
}
