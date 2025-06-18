package com.example.backendproject.purewebsocket.Controller;

import com.example.backendproject.purewebsocket.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.http.WebSocket;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;


// 웹소켓컨트롤러는 보통의 컨트롤러와 다르게 매핑이 중요하지 않음.
public class ChatWebSocketController extends TextWebSocketHandler {

    // 세션 관리용 객체
    // SynchronizedSet -> 동시성 제어용 객체
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    // json 문자열과 자바 객체 변환
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 방과 방 안의 세션을 관리하는 객체
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    // 클라이언트가 웹소켓 서버에 접속했을 때 호출됨
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        sessions.add(session);

        System.out.println("접속된 클라이언트의 세션 ID : " + session.getId());
    }

    // 클라이언트가 웹소켓 서버에 메세지를 보냈을 때 호출
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);

        // 메세지는 ObjectMapper를 통해 json 문자열을 ChatMessage타입으로 변환된다
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        String roomId = chatMessage.getRoomId();

        // 메세지를 보내고자 하는 방이 있는지 확인
        if (!rooms.containsKey(roomId)) {
            rooms.put(roomId, ConcurrentHashMap.newKeySet());
        }
        // 방에 넣어주기
        rooms.get(roomId).add(session);


        // for (WebSocketSession s : sessions) {
        for (WebSocketSession s : rooms.get(roomId)) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
            }
        }
        System.out.println(roomId + " 방에 전송된 메세지 : " + chatMessage.getMessage());
    }

    // 클라이언트가 연결 종료 시에 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        sessions.remove(session);

        for (Set<WebSocketSession> room : rooms.values()) {
            room.remove(session);
        }
    }
}

