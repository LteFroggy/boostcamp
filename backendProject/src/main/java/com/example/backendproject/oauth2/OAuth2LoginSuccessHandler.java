package com.example.backendproject.oauth2;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    // 로그인 동작을 커스텀으로 구현하고 싶을 때 사용하는 인터페이스

    // OAuth2 로그인 성공시 호출되는 메소드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {


        // 인증된 사용자의 OAuth2 정보를 가져옴 (Principal은 OAuth2User 타입으로 다운캐스팅)
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        // OAuth2User의 속성(attribute)들을 Map으로 가져옴 (카카오 프로필 정보 포함)
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // OAuth2 인증 과정에서 전달된 accessToken 추출
        String accessToken = (String) attributes.get("accessToken");
        // OAuth2 인증 과정에서 전달된 refreshToken 추출
        String refreshToken = (String) attributes.get("refreshToken");
        // 사용자 이름을 attributes에서 추출
        String name = (String) attributes.get("name");

        // 콘솔 로그로 로그인한 사용자 이름 출력 (디버깅용)
        System.out.println("[OAuth2_LOG]" + "소셯 로그인 시도한 이름 = "+name);

        // 사용자 고유 ID 저장 변수 (null 허용)
        Long id = null;
        // attributes에서 id 값을 추출
        Object idObj = attributes.get("id");
        // id가 null이 아니면 Long 타입으로 안전하게 변환
        if (idObj != null) {
            // Long 타입이 아닐 수도 있으니 안전하게 변환
            id = Long.valueOf(idObj.toString());
        }

        //토큰 전달방식
        // 또는, 보안을 강화하려면 아래처럼 HttpOnly 쿠키로 전달해도 됨
        // accessToken을 HttpOnly 쿠키로 생성
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        // 클라이언트에서 자바스크립트로 접근하지 못하도록 설정
        accessTokenCookie.setHttpOnly(true);
        // 모든 경로에서 쿠키가 유효하도록 설정
        accessTokenCookie.setPath("/");
        //  accessTokenCookie.setMaxAge(60 * 3); // 3분짜리 임시쿠키
        // accessToken 쿠키를 응답에 추가
        response.addCookie(accessTokenCookie);

        // refreshToken을 HttpOnly 쿠키로 생성
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        // 클라이언트에서 자바스크립트로 접근하지 못하도록 설정
        refreshTokenCookie.setHttpOnly(true);
        // 모든 경로에서 쿠키가 유효하도록 설정
        refreshTokenCookie.setPath("/");
        // refreshTokenCookie.setMaxAge(60 * 60 * 24); // 1일짜리
        // refreshToken 쿠키를 응답에 추가
        response.addCookie(refreshTokenCookie);


        // 로그인 성공 후 main.html로 리다이렉트하며 사용자 id 전달
        response.sendRedirect("/main.html?" + "&id=" + id);
        //  response.sendRedirect("http://localhost:3000/main" + (id  != null ? "?id=" + id : ""));//리액트
    }
}