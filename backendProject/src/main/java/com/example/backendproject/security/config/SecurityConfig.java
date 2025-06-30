package com.example.backendproject.security.config;

import com.example.backendproject.oauth2.OAuth2LoginSuccessHandler;
import com.example.backendproject.oauth2.OAuth2LogoutSuccessHandler;
import com.example.backendproject.oauth2.OAuth2UserService;
import com.example.backendproject.oauth2.RedisOAuth2AuthorizationRequestRepository;
import com.example.backendproject.security.jwt.JwtTokenFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    private final OAuth2LogoutSuccessHandler oAuth2LogoutSuccessHandler;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2UserService oAuth2UserService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new RedisOAuth2AuthorizationRequestRepository(redisTemplate);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // CSRF 보호 기능 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // Security의
                .authorizeHttpRequests((auth) ->
                        auth
                                .requestMatchers("/",
                                        "/*.html",
                                        "/favicon.ico",
                                        "/css/**",
                                        "/fetchWithAuth.js",
                                        "/js/**",
                                        "/images/**",
                                        "/.well-known/**",
                                        "/api/auth/**",
                                        "/oauth2/**",
                                        "/login/**",
                                        "/ws-gpt",
                                        "/ws-chat").permitAll() // 정적 리소스 누구나 접근

                                .requestMatchers("/api/user/**",
                                        "/boards",
                                        "/boards/**",
                                        "/api/rooms/**",
                                        "/api/comments/**").authenticated()
                        // 인증은 @AuthenticationPrincipal을 통해 인증을 받아야만 넘어갈 수 있음
                )

                .exceptionHandling(e -> e
                        // 인증이 안 된 사용자가 접근하려고 할 때
                        .authenticationEntryPoint((request, response, authException) -> {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })

                        // 인증은 되었지만, 권한이 없을 때
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                        })
                )

                // 스프링 시큐리티에서 세션관리정책을 설정하는 부분
                // 기본적으로 스프링 시큐리티는 세션을 생성함
                // 하지만 JWT 기반 인증은 세션상태를 저장하지 않는 무상태방식
                // 인증 정보를 세션에 저장하지 않고, 매 요청마다 토큰으로 인증
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 매 요청마다 적용할 필터
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2->oauth2
                .loginPage("/index.html")
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                .successHandler(oAuth2LoginSuccessHandler)
                .authorizationEndpoint(authorization -> authorization
                        .authorizationRequestRepository(authorizationRequestRepository()))
        )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(oAuth2LogoutSuccessHandler)
                        .permitAll()
                )

                // 위 명시한 설정들을 적
                .build();
    }


    @Bean // 회원가입시에 비밀번호를 암호화해주는 메소드
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
