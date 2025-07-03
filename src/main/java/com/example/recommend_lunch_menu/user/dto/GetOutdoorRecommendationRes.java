package com.example.recommend_lunch_menu.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetOutdoorRecommendationRes {
    private int rank;
    private String store;
    private String menu;
    private int price;
    private int score;
    private String comment;
}