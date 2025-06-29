package com.example.backendproject.user.service;

import com.example.backendproject.user.dto.UserDTO;
import com.example.backendproject.user.dto.UserProfileDTO;
import com.example.backendproject.user.entity.User;
import com.example.backendproject.user.entity.UserProfile;
import com.example.backendproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /** 내 정보 조회 **/
    @Transactional(readOnly = true)
    public UserDTO getMyInfo(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. "));


        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserid(user.getUserid());

        UserProfile profile = user.getUserProfile();

        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setUsername(profile.getUsername());
        profileDTO.setEmail(profile.getEmail());
        profileDTO.setPhone(profile.getPhone());
        profileDTO.setAddress(profile.getAddress());
        dto.setProfile(profileDTO);

        return dto;
    }

    /** 유저 정보 수정 **/
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO)  {
        User user = userRepository.findById(id)  //유저 레포지토리를 통해서 유저를 가져옴
                .orElseThrow(()-> new IllegalArgumentException("회원이 존재하지 않습니다. "));

        UserProfile profile = user.getUserProfile(); //프로필 객체를 만들어서 기존에 변경되기 전 프로필을 넣어줍니다.

        if (profile != null && userDTO.getProfile() != null) { //수정하려는 프로필이 있는지 체크 수정하려는 프로필정보가 있는지 체크
            UserProfileDTO dtoProfile = userDTO.getProfile();

            //프로필을  수정하기 위해 전달받은 데이터로 변경합니다.
            if (dtoProfile.getUsername() != null) profile.setUsername(dtoProfile.getUsername());
            if (dtoProfile.getEmail() != null) profile.setEmail(dtoProfile.getEmail());
            if (dtoProfile.getPhone() != null) profile.setPhone(dtoProfile.getPhone());
            if (dtoProfile.getAddress() != null) profile.setAddress(dtoProfile.getAddress());

        }

        /** JPA에서 findById()로 가져온 엔티티는 영속 상태임.
         * 필드 값을 바꾸면 JPA가 트랜잭션 커밋할 때 자동으로 update 쿼리를 날림.**/

        // 아래는 변경된 내용을 프론트에 던져주기 위해 생성합니다.
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserid(user.getUserid());
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setUsername(profile.getUsername());
        profileDTO.setEmail(profile.getEmail());
        profileDTO.setPhone(profile.getPhone());
        profileDTO.setAddress(profile.getAddress());
        dto.setProfile(profileDTO);
        return dto;

    }

}