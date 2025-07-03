package com.example.recommend_lunch_menu.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetIndoorRecommendationRes {
    private int rank;
    private String store;
    private String corner;
    private int waiting_pred;
    private int score;
    private String comment;
}
