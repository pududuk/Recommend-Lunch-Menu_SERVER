package com.example.recommend_lunch_menu.menu;

import com.example.recommend_lunch_menu.sandi.StoreType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Menu {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StoreType storeType; // ex) OUR_HOME, CJ_FRESH, PULMUONE

    @Column(nullable = false)
    private String corner; // ex) B1, B2

    @Column(nullable = false)
    private String fileName; // S3 업로드에 사용될 고유 식별자

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imgUrl;

    @Column(nullable = false)
    private String menuName; // 메뉴 이름
}
