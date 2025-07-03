package com.example.recommend_lunch_menu.sandi;

import com.example.recommend_lunch_menu.exception.BaseException;
import com.example.recommend_lunch_menu.exception.BaseResponse;
import com.example.recommend_lunch_menu.token.JwtService;
import com.example.recommend_lunch_menu.user.dto.PostUserReq;
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
    @PostMapping("/login")
    public BaseResponse<String> verifyUser(@RequestBody PostUserReq postUserReq) {
        try{
            return new BaseResponse<>(sandiService.verifyUser(postUserReq));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/table/ourhome")
    public BaseResponse<String> getOurHomeWeekdayTable() {
        try{
            return new BaseResponse<>(sandiService.getOurHomeWeekdayTable());
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/table/cjfresh")
    public BaseResponse<String> getCjFreshWeekdayTable() {
        try{
            return new BaseResponse<>(sandiService.getCjFreshWeekdayTable());
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/table/pulmuone")
    public BaseResponse<String> getPulmuoneWeekdayTable() {
        try{
            return new BaseResponse<>(sandiService.getPulmuoneWeekdayTable());
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/daily/ourhome")
    public BaseResponse<List<String>> getOurHomeDailyMenu() {
        try{
            return new BaseResponse<>(sandiService.getOurHomeDailyMenu());
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/daily/cjfresh")
    public BaseResponse<List<String>> getCjFreshDailyMenu() {
        try{
            return new BaseResponse<>(sandiService.getCjFreshDailyMenu());
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
