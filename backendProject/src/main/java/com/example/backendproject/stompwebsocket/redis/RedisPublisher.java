package com.example.backendproject.stompwebsocket.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor


public class RedisPublisher {

    private final StringRedisTemplate stringRedisTemplate;

    /** 메시지를 발행하는 클래스 **/

    ///  Stomp -> pub -> sub -> stomp
    public void publish(String channel, String message) {
        stringRedisTemplate.convertAndSend(channel, message);
    }
}
