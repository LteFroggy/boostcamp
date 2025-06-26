package com.example.backendproject.security.core;

import com.example.backendproject.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    // UserDetails <- 사용자 정보를 담는 인터페이스
    // 로그인한 사용자의 정보를 담아두는 역할을 한다.
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 유저의 권한을 반환하는 메소드
        // Collections.singleton <- 각 사용자는 하나의 권한만 가진다는 의미
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
    }

    // 토큰에서 추출한 사용자 정보의 id(=테이블의 pk값)를 반환
    public Long getId() {
        return user.getId();
    }

    @Override
    public String getPassword() {
        // User 엔티티에서 Password 반환
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // User 엔티티와 참조되어있는 Profile 엔티티의 username 반환
        return user.getUserid();
    }

    /** 아래는 현재 계정 상태를 판단하는 메소드
     * 현재는 다 True로 되어있는데, DB에 계정 상태를 확인할 수 있는
     * 값들을 만들고, 가져오도록 한다면
     * 알아서 Spring이 판단하게 할 수도 있다. **/
    @Override // 현재 계정이 활성화 상태인가?
    public boolean isEnabled() {
        return true;
    }

    @Override // 이 계정이 만료되었는가?
    public boolean isAccountNonExpired() { //
        return true;
    }

    @Override // 이 계정이 잠겨있는가?
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override // 자격증명이 만료되지는 않았는가?
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
