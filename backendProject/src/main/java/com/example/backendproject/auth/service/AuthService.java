package com.example.backendproject.auth.service;

import com.example.backendproject.auth.dto.LoginRequestDTO;
import com.example.backendproject.auth.dto.SignUpRequestDTO;
import com.example.backendproject.user.dto.UserDTO;
import com.example.backendproject.user.entity.User;
import com.example.backendproject.user.entity.UserProfile;
import com.example.backendproject.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public void signUp(SignUpRequestDTO dto) {

        // 먼저 해당 사용자가 존재하는지 조회, 존재한다면 던지기
        // 근데, 존재하지 않으면 여기서 null값을 반환해버린다. 그럼 서버가 죽음
        // 그래서 원본 리턴값을 Optional로 만들고, isPresent()로 체크해야 서버를 살리면서 체크가 가능하다.
        if (userRepository.findByUserid(dto.getUserid()).isPresent()) {
            throw new RuntimeException("사용자가 이미 존재합니다.");
        }

        User user = new User();
        user.setUserid(dto.getUserid());
        user.setPassword(dto.getPassword());

        UserProfile profile = new UserProfile();
        profile.setUsername(dto.getUsername());
        profile.setEmail(dto.getEmail());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());

        /** 연관관계 설정 **/
        profile.setUser(user);
        user.setUserProfile(profile);

        userRepository.save(user);

    }

    public UserDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByUserid(loginRequestDTO.getUserid()).orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다"));

        if (!loginRequestDTO.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUserid(user.getUserid());

        userDTO.setUsername(user.getUserProfile().getUsername());
        userDTO.setEmail(user.getUserProfile().getEmail());
        userDTO.setPhone(user.getUserProfile().getPhone());
        userDTO.setAddress(user.getUserProfile().getAddress());

        return userDTO;
    }
}
