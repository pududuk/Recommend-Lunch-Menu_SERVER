package com.example.recommend_lunch_menu.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PatchUserReq {
    private Integer age;
    private String gender;
    private Integer priceLimit;
    private String foodPreferred;
    private String foodDislike;
    private boolean localPreferred;
    private boolean tolerateWaitTime;
}
