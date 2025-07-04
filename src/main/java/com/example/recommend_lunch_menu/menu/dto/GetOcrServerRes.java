package com.example.recommend_lunch_menu.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetOcrServerRes {
    private List<MultipartFile> images;
    private List<String> corners;
    private List<String> names;
}