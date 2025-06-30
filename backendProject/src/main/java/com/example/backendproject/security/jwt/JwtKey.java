package com.example.backendproject.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtKey {

    // jwt 서명에 사용될 개인 키
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Bean
    public SecretKey secretKey() {
        // 설정파일에서 불러온 키를 바이트 배열로 변환
        byte[] keyBytes = secretKey.getBytes();
        // 바이트 배열을 HmacSHA256용 Security 객체로 매핑
        return new SecretKeySpec(keyBytes, "HmacSHA512");
    }


}
