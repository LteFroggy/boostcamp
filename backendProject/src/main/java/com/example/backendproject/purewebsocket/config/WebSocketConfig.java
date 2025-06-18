package com.example.backendproject.purewebsocket.config;

import com.example.backendproject.purewebsocket.Controller.ChatWebSocketController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

// @Configuration
// @EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // ws-chat 앤드포인트로 요청을 보낼 수 있는지 결정하는 보안 정책 결정
        // *은 모든 도메인에서 접근 가능하다는 뜻
        registry.addHandler(new ChatWebSocketController(), "/ws-chat").setAllowedOriginPatterns("*");
    }
}