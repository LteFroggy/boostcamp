package com.example.backendproject.stompwebsocket.controller;

import com.example.backendproject.stompwebsocket.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

/*    // chat,sendMessage로 메세지를 보내면, Public이라는 토픽을 구독한 사람들에게 뿌려준다.
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage message) {
        return message;
    }*/

    // 서버가 클라이언트에게 수동으로 메세지를 보낼 수 있도록 하는 클래스
    private final SimpMessagingTemplate template;

    // 이 메서드가 반환하는 메시지를 "/topic/public"을 구독하고 있는 모든 클라이언트에게 전송
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage message) {
        if (message.getTo() != null && !message.getTo().isEmpty()) {
            // 귓속말
            // 내 아이디로 귓속말 경로를 활성화한다
            template.convertAndSendToUser(message.getTo(), "/queue/private", message);
        } else {
            template.convertAndSend("/topic/" + message.getRoomId(), message);
        }
    }
}