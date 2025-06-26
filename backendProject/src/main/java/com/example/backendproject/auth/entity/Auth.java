package com.example.backendproject.auth.entity;

import com.example.backendproject.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tokenType;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    // 테이블과 테이블을 연결, 1:1관계에서는 주인쪽만 패치전략 적용됨
    @OneToOne(fetch = FetchType.LAZY) // 지연로딩 적용 -> Auth 엔티티 조회시에 user객체는 불러오지 않는다.
    @JoinColumn(name = "user_id") // Auth.getUser()에 실제 접근 시에 User쿼리 발생!
    private User user;


    public Auth(User user, String accessToken, String refreshToken, String tokenType) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
    }

    // updateAccessToken 메소드 추가
    // 토큰값을 업데이트 해주는 메소드
    public void updateAccessToken(String newAccessToken) {
        this.accessToken = newAccessToken;
    }

    // updateRefreshToken 메소드 추가
    // 리프레시 토큰 값을 업데이트해주는 메소드
    public void updateRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }

}
