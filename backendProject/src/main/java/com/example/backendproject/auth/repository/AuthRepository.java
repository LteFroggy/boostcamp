package com.example.backendproject.auth.repository;

import com.example.backendproject.auth.entity.Auth;
import com.example.backendproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long>{

    // 유저에 해당하는 Authorization 정보가 있는지 조회
    boolean existsByUser(User user);

    // refreshtoken 바탕으로 인증정보가 있는지 조회
    Optional<Auth> findByRefreshToken(String refreshToken);
}
