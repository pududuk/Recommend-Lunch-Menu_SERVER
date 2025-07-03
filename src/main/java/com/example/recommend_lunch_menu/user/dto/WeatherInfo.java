package com.example.recommend_lunch_menu.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WeatherInfo {
    private Integer sky; // 하늘 상태 -> 맑음(1), 구름많음(3), 흐림(4)
    private Integer pop; // 강수 확률(%)
    private Integer pty; // 강수 형태 -> 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5)
}
