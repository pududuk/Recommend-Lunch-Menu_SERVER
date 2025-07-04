package com.example.recommend_lunch_menu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@EnableAsync
@SpringBootApplication
public class RecommendLunchMenuApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecommendLunchMenuApplication.class, args);
	}

}
