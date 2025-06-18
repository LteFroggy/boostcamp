package com.example.backendproject.stompwebsocket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String message;

    // 귓속말 보낼 사람
    private String from;

    // 귓속말 받을 사람
    private String to;
    private String roomId;
}
