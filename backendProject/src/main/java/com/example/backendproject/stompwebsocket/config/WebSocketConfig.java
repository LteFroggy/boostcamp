package com.example.backendproject.stompwebsocket.config;

import com.example.backendproject.stompwebsocket.handler.CustomHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker  // WebSocket 메시지 브로커를 활성화 (STOMP 사용 가능하게 함)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 클라이언트로 전달할 때 사용하는 경로(prefix)
        // 클라이언트는 이 경로를 구독해서 메시지를 수신한다.
        registry.enableSimpleBroker("/topic", "/queue"); // 구독용 경로

        // 클라이언트가 메시지를 보낼 때 사용하는 경로(prefix)
        // 예: 클라이언트가 /app/chat.sendMessage로 메시지를 보내면
        // 서버에서는 @MessageMapping("/chat.sendMessage")로 처리한다.
        registry.setApplicationDestinationPrefixes("/app");

        // /user 특정 사용자에게 메세지를 보낼 접두어
        /* 서버가 특정 사용자에게 메시지를 보낼 때, 클라이언트가 구독할 경로 접두어 */
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 연결을 위한 endpoint를 설정
        // 클라이언트는 ws://서버주소/ws-chat 으로 연결한다.
        // CORS 문제 방지를 위해 모든 origin 허용
        registry.addEndpoint("/ws-chat")
                .setHandshakeHandler(new CustomHandshakeHandler())
                .setAllowedOriginPatterns("*");

        registry.addEndpoint("ws-gpt")
                .setAllowedOriginPatterns("*");
    }
}
