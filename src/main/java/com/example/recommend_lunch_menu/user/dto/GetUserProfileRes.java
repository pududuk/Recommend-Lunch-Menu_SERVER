package com.example.recommend_lunch_menu.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetUserProfileRes {
    private int age;
    private String gender;
    private boolean localPreferred;
    private boolean tolerateWaitTime;
    private String foodPreferred;
    private String foodDislike;
    private int priceLimit;
}