package com.example.recommend_lunch_menu.schedule.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "admin")
public class AdminInfo {
    private String username;
    private String password;
    private String clientId;
}
