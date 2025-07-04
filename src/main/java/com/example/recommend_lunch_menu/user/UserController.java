package com.example.recommend_lunch_menu.user;

import com.example.recommend_lunch_menu.exception.BaseException;
import com.example.recommend_lunch_menu.exception.BaseResponse;
import com.example.recommend_lunch_menu.token.JwtService;
import com.example.recommend_lunch_menu.user.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PatchMapping("/profile")
    @Operation(summary = "유저의 성별, 나이, 대기시간, 근거리, 선호/비선호 음식 정보 설정", description = "유저의 기본 프로필을 설정하기 위한 API")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    @Parameters({
            @Parameter(name = "age", description = "나이", example = "27"),
            @Parameter(name = "gender", description = "male or female", example = "male"),
            @Parameter(name = "localPreferred", description = "근거리 선호 여부", example = "false"),
            @Parameter(name = "tolerateWaitTime", description = "대기시간 괜찮은지 여부", example = "false"),
            @Parameter(name = "foodPreferred", description = "음식 이름을 콤마로 구분", example = "파스타, 스테이크, 삼겹살"),
            @Parameter(name = "foodDislike", description = "음식 이름을 콤마로 구분", example = "가지, 오이"),
            @Parameter(name = "priceLimit", description = "음식의 가격 상한", example = "15000"),
    })
    public BaseResponse<String> setUserProfile(@RequestBody PatchUserReq patchUserReq) {
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.setUserProfile(userId, patchUserReq));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "유저의 성별, 나이, 대기시간, 근거리, 선호/비선호 음식 정보 조회", description = "유저의 기본 프로필을 조회하기 위한 API")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    public BaseResponse<GetUserProfileRes> getUserProfile() {
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.getUserProfile(userId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/indoor")
    @Operation(summary = "사내 음식 추천", description = "사내 음식 추천 결과를 보여주는 API")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    public BaseResponse<List<GetIndoorRecommendationRes>> getIndoorRecommendation() {
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.getIndoorRecommendation(userId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        } catch (URISyntaxException exception) {
            return null; // URI Parse Error
        }
    }

    @GetMapping("/outdoor")
    @Operation(summary = "사외 음식 추천", description = "사외 음식 추천 결과를 보여주는 API")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    public BaseResponse<List<GetOutdoorRecommendationRes>> getOutdoorRecommendation() {
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.getOutdoorRecommendation(userId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        } catch (URISyntaxException exception) {
            return null; // URI Parse Error
        }
    }

}
