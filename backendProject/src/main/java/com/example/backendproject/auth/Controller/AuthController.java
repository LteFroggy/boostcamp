package com.example.backendproject.auth.Controller;

import com.example.backendproject.auth.dto.LoginRequestDTO;
import com.example.backendproject.auth.dto.LoginResponseDTO;
import com.example.backendproject.auth.dto.SignUpRequestDTO;
import com.example.backendproject.auth.service.AuthService;
import com.example.backendproject.user.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /** 회원가입 **/
    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        try {
            authService.signUp(signUpRequestDTO);
            return ResponseEntity.ok("회원가입 성공");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            authService.login(loginRequestDTO);
            UserDTO loginUser = authService.login(loginRequestDTO);

            System.out.println("로그인 성공 = " + new ObjectMapper().writeValueAsString(loginUser));

            return ResponseEntity.ok(loginUser);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    */

    /** 로그인 **/
    @PostMapping("/loginSecurity")
    public ResponseEntity<?> loginSecurity(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponseDTO = authService.login(loginRequestDTO);
        return ResponseEntity.ok(loginResponseDTO);
    }

    /** 토큰갱신 API */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader(value = "Authorization", required = false)
                                          String authorizationHeader, HttpServletRequest request) {
        String refreshToken = null;

        // 1. 쿠키에서 찾기
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }
        // 2. Authorization 헤더 찾기
        if (refreshToken == null && authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            refreshToken = authorizationHeader.replace("Bearer ", "").trim();
        }


        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("리프레시 토큰이 없습니다.");
        }

        String newAccessToken = authService.refreshToken(refreshToken);

        // json객체로 변환하여 Front로 내려주기
        Map<String, String> res = new HashMap<>();
        res.put("accessToken", newAccessToken);
        res.put("refreshToken", refreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}