package com.example.recommend_lunch_menu.sandi;

import com.example.recommend_lunch_menu.exception.BaseException;
import com.example.recommend_lunch_menu.exception.BaseResponse;
import com.example.recommend_lunch_menu.token.JwtService;
import com.example.recommend_lunch_menu.user.dto.PostUserReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sandi")
public class SandiController {

    private final SandiService sandiService;
    private final JwtService jwtService;

    // Check Sandi Account Validation
    @Operation(summary = "Sandi 유저 인증", description = "유저의 초기 로그인을 위한 API")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    @Parameters(value = {
            @Parameter(name = "email", description = "Sandi 메일 계정", example = "hyunseop.byun@lge.com"),
            @Parameter(name = "password", description = "Sandi 비밀번호", example = "abcd1234")
    })
    @PostMapping("/login")
    public BaseResponse<String> verifyUser(@RequestBody PostUserReq postUserReq) {
        try{
            return new BaseResponse<>(sandiService.verifyUser(postUserReq));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @Operation(summary = "아워홈 주간 식단표", description = "아워홈 주간 식단표 이미지 URL을 저장하는 API")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    @GetMapping("/table/ourhome")
    public BaseResponse<String> getOurHomeWeekdayTable() {
        try{
            return new BaseResponse<>(sandiService.getOurHomeWeekdayTable());
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @Operation(summary = "CJ 주간 식단표", description = "CJ 주간 식단표 이미지 URL을 저장하는 API")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    @GetMapping("/table/cjfresh")
    public BaseResponse<String> getCjFreshWeekdayTable() {
        try{
            return new BaseResponse<>(sandiService.getCjFreshWeekdayTable());
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @Operation(summary = "풀무원 주간 식단표", description = "풀무원 주간 식단표 이미지 URL을 저장하는 API")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    @GetMapping("/table/pulmuone")
    public BaseResponse<String> getPulmuoneWeekdayTable() {
        try{
            return new BaseResponse<>(sandiService.getPulmuoneWeekdayTable());
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @Operation(summary = "아워홈 일간 메뉴", description = "아워홈 일간 메뉴 이미지 URL을 리스트 형태로 저장하는 API")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    @GetMapping("/daily/ourhome")
    public BaseResponse<List<String>> getOurHomeDailyMenu() {
        try{
            return new BaseResponse<>(sandiService.getOurHomeDailyMenu());
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @Operation(summary = "CJ 일간 메뉴", description = "CJ 일간 메뉴 이미지 URL을 리스트 형태로 저장하는 API")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    @GetMapping("/daily/cjfresh")
    public BaseResponse<List<String>> getCjFreshDailyMenu() {
        try{
            return new BaseResponse<>(sandiService.getCjFreshDailyMenu());
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
