package com.example.recommend_lunch_menu.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PatchUserReq {
    private Integer age;
    private String gender;
    private boolean isLocalPreferred;
    private boolean isTolerateWaitTime;
    private String foodPreferred;
    private String foodDislike;
}
