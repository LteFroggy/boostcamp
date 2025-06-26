package com.example.backendproject.auth.dto;

import com.example.backendproject.auth.entity.Auth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Long userId;

    // Constructor
    public LoginResponseDTO(Auth auth) {
        this.tokenType = auth.getTokenType();
        this.accessToken = auth.getAccessToken();
        this.refreshToken = auth.getRefreshToken();
        this.userId = auth.getId();
    }

}
