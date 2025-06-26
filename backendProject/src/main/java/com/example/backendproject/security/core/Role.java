package com.example.backendproject.security.core;

import lombok.Getter;

// 회원가입시에 사용자의 권한을 정의
// 일반 유저인지, 관리자인지 구분
@Getter
public enum Role {

    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");
    private String role;

    Role(String role) {
        this.role = role;
    }


}
