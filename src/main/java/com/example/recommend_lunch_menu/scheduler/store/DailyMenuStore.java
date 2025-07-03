package com.example.recommend_lunch_menu.scheduler.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DailyMenuStore {
    private List<String> ourHomeImgUrls;
    private List<String> cjFreshImgUrls;
}
