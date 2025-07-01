package com.example.recommend_lunch_menu.scheduler;

import com.example.recommend_lunch_menu.sandi.SandiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ScheduleController {

    private final SandiService sandiService;

    @Scheduled(cron = "0 0 11-17 * * 1-5", zone = "Asia/Seoul") // 월 ~ 금 오전 11시부터 오후 5시까지 1시간마다 반복
    public void issueAccessToken() { // Issue Sandi App OAuth Token
        log.info("<-------------Sandi 앱 로그인 작업을 수행합니다.------------->");
        sandiService.issueAccessToken();
        log.info("<-------------Sandi 앱 로그인 작업이 완료되었습니다.------------->");
    }

    @Scheduled(cron = "0 */30 8-10 * * 1-2", zone = "Asia/Seoul") // 월, 화 오전 8시 ~ 10시까지 30분마다 반복
    public void getOurHomeWeekdayTable() { // 주간 식단표 Update
        log.info("<-------------아워홈 주간 식단표 이미지를 로드합니다.------------->");
        sandiService.getOurHomeWeekdayTable();
        log.info("<-------------아워홈 주간 식단표 이미지 로드가 완료되었습니다.------------->");
    }

    @Scheduled(cron = "0 */30 8-10 * * 1-2", zone = "Asia/Seoul") // 월, 화 오전 8시 ~ 10시까지 30분마다 반복
    public void getCjFreshWeekdayTable() { // 주간 식단표 Update
        log.info("<-------------CJ 주간 식단표 이미지를 로드합니다.------------->");
        sandiService.getCjFreshWeekdayTable();
        log.info("<-------------CJ 주간 식단표 이미지 로드가 완료되었습니다.------------->");
    }

    @Scheduled(cron = "0 */30 8-10 * * 1-2", zone = "Asia/Seoul") // 월, 화 오전 8시 ~ 10시까지 30분마다 반복
    public void getPulmuoneWeekdayTable() { // 주간 식단표 Update
        log.info("<-------------풀무원 주간 식단표 이미지를 로드합니다.------------->");
        sandiService.getCjFreshWeekdayTable();
        log.info("<-------------풀무원 주간 식단표 이미지 로드가 완료되었습니다.------------->");
    }
}