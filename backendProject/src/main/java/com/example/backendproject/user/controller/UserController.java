package com.example.backendproject.user.controller;


import com.example.backendproject.security.core.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import com.example.backendproject.user.dto.UserDTO;
import com.example.backendproject.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user") //변경
@RequiredArgsConstructor
public class UserController {

    //삭제
//    @Value("${PROJECT_NAME:web Server}")
//    private String instansName;
//
//    @GetMapping
//    public String test(){
//        return instansName;
//    }

    private final UserService userService;

    /** 내 정보 보기 **/
    // 인증된 사용자만 접근 가능하도록 수정
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long id = customUserDetails.getId();
        return ResponseEntity.ok(userService.getMyInfo(id));
    }

    /** 유저 정보 수정 **/
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateUser(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody UserDTO userDTO)  {
        Long id = customUserDetails.getId();
        UserDTO updated = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updated);
    }


}