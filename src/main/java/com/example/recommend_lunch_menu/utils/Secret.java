package com.example.recommend_lunch_menu.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "key")
public class Secret {
    private String aesKey;
    private String serviceKey;
}

