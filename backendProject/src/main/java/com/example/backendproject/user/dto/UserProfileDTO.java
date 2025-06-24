package com.example.backendproject.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

    private String username;
    private String email;
    private String phone;
    private String address;

}
