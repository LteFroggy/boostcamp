package com.example.backendproject.security.jwt;

import com.example.backendproject.security.core.CustomUserDetails;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    /** JWT 토큰 생성 및 추출 검증하는 클래스 **/

    private final SecretKey secretKey;

    // 현재 인증된 사용자 정보를 기반으로 access, refresh token 발급
    // 토큰이 발급되는 일체의 과정이다.
    public String generateToken(Authentication authentication, Long expirationMillis) {

        // 현재 로그인한 사용자의 정보를 꺼냄
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        Date expireDate = new Date(new Date().getTime() + expirationMillis);

        Claims claims = Jwts.claims();
        claims.put("user-id", userDetails.getId());
        claims.put("username", userDetails.getUsername());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())  // 이 JWT 토큰의 주체를 지정
                .setClaims(claims)                      // PayLoad
                .setIssuedAt(new Date())                // 토큰 발급 시간
                .setExpiration(expireDate)              // 토근 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS512)  // 시크릿 키와 알고리즘을 이용해 암호화해 사용
                .compact();                             // <- 에서 발급된 정보들을 최종적으로 문자열로 만들어주는 메소드

    }

    // JWT 토큰에서 사용자의 ID를 추출해 HTTP 요청에 포함시켜 보내기 위한 메소드
    public Long getUserIdFromToken(String token) {
        return Jwts
                .parserBuilder()                    // JWT 토큰을 해석하겠다고 선언
                .setSigningKey(secretKey)           // 토큰을 검증하기 위해 비밀키 사용
                .build()                            // 해석할 준비 완료
                .parseClaimsJws(token)              // 전달 받은 토큰을 파싱
                .getBody()                          // 파싱한 토큰의 payload부분을 꺼냄
                .get("user-id", Long.class);     // user-id를 반환
    }


    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)   //
                    .build()                    //
                    .parseClaimsJws(token);     //
            return true;
        } catch (MalformedJwtException e) {
            // 토큰 형식이 잘못되었을 때
            return false;
        } catch (ExpiredJwtException e) {
            // 토큰이 만료되었을 때
            return false;
        } catch (UnsupportedJwtException e) {
            // 지원하지 않는 토큰일 때
            return false;
        } catch (IllegalArgumentException e) {
            // 토큰 문자열이 이상하거나 비어있을 때
            return false;
        } catch (JwtException e) {
            // 기타 예외
            return false;
        }
    }
}
