package com.example.recommend_lunch_menu.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetUserPreferenceRes {
    Integer age;
    String gender;
    String foodPreferred;
    String foodDislike;
    Integer waitLimit;
    Integer distance;
    Integer priceLimit;
    Map<String, Integer> weatherInfo;
}