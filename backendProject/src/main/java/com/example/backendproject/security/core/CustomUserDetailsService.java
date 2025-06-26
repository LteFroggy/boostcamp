package com.example.backendproject.security.core;

import com.example.backendproject.user.entity.User;
import com.example.backendproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        User user = userRepository.findByUserid(userid).orElseThrow(
                () -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다 -> " + userid)
        );

        return new CustomUserDetails(user);
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id).orElseThrow (
                () -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다 -> " + id)
        );

        return new CustomUserDetails(user);
    }

}
