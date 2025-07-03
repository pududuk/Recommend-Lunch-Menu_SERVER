package com.example.recommend_lunch_menu.user.dto;

import com.example.recommend_lunch_menu.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostUserReq {
    private String email;
    private String password;
}
