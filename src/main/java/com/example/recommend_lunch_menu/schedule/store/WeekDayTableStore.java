package com.example.recommend_lunch_menu.schedule.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WeekDayTableStore {
    private String ourHomeTableImgUrl;
    private String cjFreshTableImgUrl;
    private String pulmuoneTableImgUrl;
}
