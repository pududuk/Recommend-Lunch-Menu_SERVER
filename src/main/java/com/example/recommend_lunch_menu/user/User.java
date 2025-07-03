package com.example.recommend_lunch_menu.user;

import com.example.recommend_lunch_menu.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseTimeEntity {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // 고유 식별자

    @Column(nullable = false)
    private String email; // Sandi ID

    @Column(nullable = false)
    private String password; // Sandi 패스워드

    @Column(nullable = false, columnDefinition = "TEXT")
    private String accessToken; // Sandi Oauth Token

    @Column(nullable = true)
    private Integer age;

    @Column(nullable = true)
    private String gender; // Man, Woman

    @Column(nullable = true)
    private boolean isLocalPreferred; // 지역성에 대한 선호도

    @Column(nullable = true)
    private boolean isTolerateWaitTime; // 대기시간에 대한 선호도

    @Column(nullable = true)
    private String foodPreferred;

    @Column(nullable = true)
    private String foodDislike;

    // Admin 설정
    @Column(nullable = false)
    private boolean isAdmin = false;

}
