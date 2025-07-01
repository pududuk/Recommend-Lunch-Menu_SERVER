package com.example.recommend_lunch_menu.sandi;

import com.example.recommend_lunch_menu.exception.BaseException;
import com.example.recommend_lunch_menu.exception.BaseResponse;
import com.example.recommend_lunch_menu.scheduler.dto.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sandi")
public class SandiController {

    private final SandiService sandiService;

    @PostMapping("/login")
    public BaseResponse<String> loginUser() {
        try{
            return new BaseResponse<>(sandiService.issueAccessToken());
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/ourhome")
    public BaseResponse<String> getOurHomeWeekdayTable() {
        try{
            return new BaseResponse<>(sandiService.getOurHomeWeekdayTable());
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/cjfresh")
    public BaseResponse<String> getCjFreshWeekdayTable() {
        try{
            return new BaseResponse<>(sandiService.getCjFreshWeekdayTable());
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/pulmuone")
    public BaseResponse<String> getPulmuoneWeekdayTable() {
        try{
            return new BaseResponse<>(sandiService.getPulmuoneWeekdayTable());
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
