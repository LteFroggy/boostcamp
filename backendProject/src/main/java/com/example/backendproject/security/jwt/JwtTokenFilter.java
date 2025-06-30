package com.example.backendproject.security.jwt;

import com.example.backendproject.security.core.CustomUserDetailsService;
import com.example.backendproject.threadlocal.TraceIdHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    /// JwtTokenFilter          사용자의 HTTP 요청을 가로채서, 토큰의 유효성을 검증하는 필터 클래스
    /// OncePerRequestFilter    한 요청당 단 한번만 실행되는 필터의 역할이다

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;


    @Override // HTTP 매 요청마다 호출
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // HTTP 요청이 시작되는 구간에서 TraceID 발급
            String traceId = UUID.randomUUID().toString().substring(0, 8);
            TraceIdHolder.set(traceId); // TraceID ThreadLocal에 저장.


            String accessToken = getTokenFromRequest(request); // 요청 헤더에서 토큰 추출=

            if (accessToken != null &&
                    jwtTokenProvider.validateToken(accessToken)) { // 토큰 잘 있고, Validation을 완료했다면

                // 토큰에서 사용자를 꺼내서 담은 사용자 인증 객체
                UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(accessToken);

                // http 요청으로부터 부가 정보(ip, 세션 등)를 추출해서 사용자 인증 객체에 넣어줌
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 토큰에서 사용자 인증정보를 조회해서 인증정보를 현재 스레드에 인증된 사용자로 등록
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                String url = request.getRequestURI();
                String method = request.getMethod();
                log.info("현재 들어온 HTTP 요청 : {} - {}", url, method);
                log.info("토큰 인증 성공 : {}", accessToken);
            } else {
                if (!request.getRequestURI().equals("/actuator/prometheus")) {
                    log.info("현재 들어온 요청 URI: {}", request.getRequestURI());
                    log.info("X 토큰 없음 또는 유효하지 않음 : {}", accessToken);
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            // HTTP 요청이 끝날 때 ThreadLocal 데이터를 비워준다.
            TraceIdHolder.clear();
            if (!request.getRequestURI().equals("/actuator/prometheus")) {
                log.info("TraceIdHolder 데이터 삭제 후 값 : {}", TraceIdHolder.get());
            }
        }

        // JwtTokenFilter를 거치고 다음 필터로 넘어감

        /** 필터의 종류는 아래와 같음
         * CharacterEncodingFilter  문자 인코딩 처리
         * CorsFilter               CORS 정책 처리
         * CsrfFilter               CSRF보안 처리
         * JWTTokenFilter           JWT 토큰 처리
         * SecurityContextFilter    인증/인가 정보 저장
         * ExceptionFilter          예외 처리
         */
    }



    // HTTP 요청에서 Token을 추출하는 메소드
    public String getTokenFromRequest(HttpServletRequest request) {

        String token = null;

        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }

        return token;
    }


    // http 요청에서 사용자 정보를 담는 객체
    private UsernamePasswordAuthenticationToken getAuthentication(String token) {

        // JTW 토큰
        Long userid = jwtTokenProvider.getUserIdFromToken(token);

        // 위 추출한 id를 DB에서 사용자 정보 조회
        UserDetails userDetails = customUserDetailsService.loadUserById(userid);

        return new UsernamePasswordAuthenticationToken(
                userDetails,        // 사용자 정보
                null,
                userDetails.getAuthorities()    // 사용자의 권한
        );
    }

}
