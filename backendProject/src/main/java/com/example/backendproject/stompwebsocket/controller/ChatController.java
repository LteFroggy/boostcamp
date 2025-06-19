package com.example.backendproject.stompwebsocket.controller;

import com.example.backendproject.stompwebsocket.dto.ChatMessage;
import com.example.backendproject.stompwebsocket.redis.RedisPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
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

    //동적으로 방 생성 가능
    @Value("${PROJECT_NAME:web Server}")
    private String instansName;

    // 서버가 클라이언트에게 수동으로 메세지를 보낼 수 있도록 하는 클래스
    private final SimpMessagingTemplate template;

    private final RedisPublisher redisPublisher;
    private ObjectMapper objectMapper = new ObjectMapper();

   // 이 메서드가 반환하는 메시지를 "/topic/public"을 구독하고 있는 모든 클라이언트에게 전송
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage message) throws JsonProcessingException {
        message.setMessage(instansName + " " + message.getMessage());

        /*

        if (message.getTo() != null && !message.getTo().isEmpty()) {
            // 귓속말
            // 내 아이디로 귓속말 경로를 활성화한다
            template.convertAndSendToUser(message.getTo(), "/queue/private", message);
        } else {
            template.convertAndSend("/topic/" + message.getRoomId(), message);
        }
        */

        String channel = null;
        String msg = null;


        if (message.getTo() != null && message.getTo().isEmpty()) {
            channel = "private." + message.getRoomId();
            msg = objectMapper.writeValueAsString(message);
        }

        else {
            channel = "room." + message.getRoomId();
            msg = objectMapper.writeValueAsString(message);
        }
        redisPublisher.publish(channel, msg);
    }
}