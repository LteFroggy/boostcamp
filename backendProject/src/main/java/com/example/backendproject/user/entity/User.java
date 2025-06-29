package com.example.backendproject.user.entity;

import com.example.backendproject.auth.entity.Auth;
import com.example.backendproject.board.entity.Board;
import com.example.backendproject.security.core.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class User extends BaseTime{

    // ID는 PK임을 의미함
    // IDENTITY는 Auto-incresement
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userid;

    @Column(nullable = false)
    private String password;

    // 0625 10:15 수정
    // 이 필드를 DB에 문자열로 저장하라는 의미이다.
    @Enumerated(EnumType.STRING)
    private Role role;

    // mappedBy는 나를 참조하고 있는 field명, UserProfile에서 Field명을 user로 작성했기에 이리 씀
    // Cascade는 원문 삭제하면 키도 사라지기
    // fetch는 지연 방식(추가한 후에 유저가 바로 불러와지지 않고, 유저를 호출해야 불러와짐)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Auth auth;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Board> boards = new ArrayList<>();

}
