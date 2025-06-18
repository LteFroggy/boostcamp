package com.example.backendproject.purewebsocket.room.controller;

import com.example.backendproject.purewebsocket.room.entity.ChatRoom;
import com.example.backendproject.purewebsocket.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
// 이건 일반 컨트롤러이니 매핑을 수행한다.
public class RoomController {
    private final RoomService roomService;

    // api용이긴 하다. Get하면 모든 룸들을 표시
    @GetMapping
    public List<ChatRoom> getAllRoom(String roomId) {
        return roomService.findAllRooms();
    }

    // 방 이름과 함께 Post하면, rommService객체와 함께 룸을 만든다.
    @PostMapping("/{roomId}")
    public ChatRoom createRoom(@PathVariable String roomId) {
        return roomService.createRoom(roomId);
    }
}
