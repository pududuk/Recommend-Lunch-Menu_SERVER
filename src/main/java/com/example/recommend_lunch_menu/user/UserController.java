package com.example.recommend_lunch_menu.user;

import com.example.recommend_lunch_menu.exception.BaseException;
import com.example.recommend_lunch_menu.exception.BaseResponse;
import com.example.recommend_lunch_menu.token.JwtService;
import com.example.recommend_lunch_menu.user.dto.GetIndoorRecommendationRes;
import com.example.recommend_lunch_menu.user.dto.PatchUserReq;
import com.example.recommend_lunch_menu.user.dto.PostUserReq;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    // Set User Profile
    @PatchMapping("/profile")
    public BaseResponse<String> setUserProfile(@RequestBody PatchUserReq patchUserReq) {
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.setUserProfile(userId, patchUserReq));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // Indoor Recommendation
    @GetMapping("/indoor")
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

}
