package com.example.recommend_lunch_menu.menu;

import com.example.recommend_lunch_menu.exception.BaseException;
import com.example.recommend_lunch_menu.exception.BaseResponse;
import com.example.recommend_lunch_menu.menu.dto.GetOcrServerRes;
import com.example.recommend_lunch_menu.sandi.StoreType;
import com.example.recommend_lunch_menu.user.dto.PostUserReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://client")
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "OCR Server로부터 이미지 파일, 코너, 메뉴 이름을 받음.", description = "OCR Server의 결과를 받기 위한 API")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    @Parameters(value = {
            @Parameter(name = "images", description = "이미지 파일 리스트", example = "[아워홈_B1_일간메뉴.png, 아워홈_B3_일간메뉴.png]"),
            @Parameter(name = "corners", description = "코너 이름 리스트", example = "['B1', 'B2']"),
            @Parameter(name = "names", description = "메뉴 이름 리스트", example = "['김치찌개', '된장찌개']")
    })
    @PostMapping("/ocr-response")
    public BaseResponse<String> processOcrResponse(@RequestBody GetOcrServerRes getOcrServerRes) {
        try{
            return new BaseResponse<>(menuService.processOcrResponse(getOcrServerRes));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
