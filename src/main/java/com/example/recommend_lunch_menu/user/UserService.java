package com.example.recommend_lunch_menu.user;

import com.example.recommend_lunch_menu.exception.BaseException;
import com.example.recommend_lunch_menu.exception.BaseResponseStatus;
import com.example.recommend_lunch_menu.sandi.SandiService;
import com.example.recommend_lunch_menu.user.dto.*;
import com.example.recommend_lunch_menu.utils.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@EnableTransactionManagement
@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UtilService utilService;
    private final SandiService sandiService;
    private final Secret secret;

    private static final int WAIT_TIME_INTOLERANCE = 12;
    private static final int WAIT_TIME_TOLERANCE = 30;

    private static final int SHORT_DISTANCE = 1000;
    private static final int LONG_DISTANCE = 2000;

    @Transactional
    public String setUserProfile(Long userId, PatchUserReq patchUserReq) throws BaseException {
        User user = utilService.findByUserIdWithValidation(userId);
        user.setAge(patchUserReq.getAge());
        user.setGender(patchUserReq.getGender());
        user.setLocalPreferred(patchUserReq.isLocalPreferred());
        user.setTolerateWaitTime(patchUserReq.isTolerateWaitTime());
        user.setFoodPreferred(patchUserReq.getFoodPreferred());
        user.setFoodDislike(patchUserReq.getFoodDislike());
        user.setPriceLimit(patchUserReq.getPriceLimit());

        return "프로필 설정이 완료되었습니다.";
    }

    @Transactional
    public GetUserProfileRes getUserProfile(Long userId) throws BaseException {
        User user = utilService.findByUserIdWithValidation(userId);
        int age = user.getAge();
        String gender = user.getGender();
        boolean localPreferred = user.isLocalPreferred();
        boolean tolerateWaitTime = user.isTolerateWaitTime();
        String foodPreferred = user.getFoodPreferred();
        String foodDislike = user.getFoodDislike();
        int priceLimit = user.getPriceLimit();

        return new GetUserProfileRes(age, gender, localPreferred, tolerateWaitTime, foodPreferred, foodDislike, priceLimit);
    }

    @Transactional
    public List<GetIndoorRecommendationRes> getIndoorRecommendation(Long userId) throws BaseException, URISyntaxException {
        GetUserPreferenceRes getUserPreferenceRes = getUserPreference(userId);
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디 설정
        Map<String, String> body = new HashMap<>();
        body.put("age", getUserPreferenceRes.getAge().toString());
        body.put("gender", getUserPreferenceRes.getGender());
        body.put("weather", getUserPreferenceRes.getWeatherInfo());
        body.put("preferred", getUserPreferenceRes.getFoodPreferred());
        body.put("disliked", getUserPreferenceRes.getFoodDislike());
        body.put("waiting_limit", Integer.toString(getUserPreferenceRes.getWaitLimit()));

        try {
            /* <--------------------- Dummy -------------------> */
            String answer = "rank,store,corner,menu,waiting_pred,score,comment\n1,ourhome,A3/C4,후랑크모듬채소볶음/간장찜닭/계란찜/시금치나물/배추김치/흑미밥/쌀밥,15,95,고기와 찜닭, 든든한 선택!\n2,ourhome,A1/2 C1/2,후랑크모듬채소볶음/어묵국/야채계란말이/쌀밥/배추김치,13,85,고기를 포함한 맛있는 점심!\n3,ourhome,C3/4,감자국/바싹불고기/김치볶음/오징어젓무침/브로콜리된장무침/도토리묵/배추김치/쌀밥/흑미밥,16,80,고소한 불고기가 기다립니다!\n4,ourhome,B3,불고기철판구이/된장찌개/중화두부조림/사라다/미역줄기볶음,24,75,뜨끈한 불고기로 기력 보충!\n5,ourhome,B1,불고기철판구이/된장찌개/중화두부조림/사라다/미역줄기볶음,24,75,흐린 날씨에 맞는 따뜻한 메뉴!";
            List<GetIndoorRecommendationRes> resultList = new ArrayList<>();
            // 줄 단위로 나누기
            String[] lines = answer.split("\n");

            if (lines.length > 1) {
                // 첫 줄은 헤더
                String[] header = lines[0].split(",");

                for (int i = 1; i < lines.length; i++) {
                    String[] values = lines[i].split(",", -1);
                    if (values.length < 6) continue;

                    GetIndoorRecommendationRes res = new GetIndoorRecommendationRes();
                    res.setRank(Integer.parseInt(values[0].trim()));
                    res.setStore(values[1].trim());
                    res.setCorner(values[2].trim());
                    res.setMenu(values[3].trim());
                    res.setWaiting_pred(Integer.parseInt(values[4].trim()));
                    res.setScore(Integer.parseInt(values[5].trim()));
                    res.setComment(values[6].trim());

                    resultList.add(res);
                }

                return resultList;
            } else {
                throw new BaseException(BaseResponseStatus.INVALID_JWT);
            }
            /* <--------------------- Dummy -------------------> */

//            RestTemplate restTemplate = new RestTemplate();
//            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
//            ResponseEntity<String> responseEntity = restTemplate.exchange(
//                    Constants.AI_SERVER_URL + "/indoor/recommend",
//                    HttpMethod.POST,
//                    requestEntity,
//                    String.class
//            );
//
//            String response = responseEntity.getBody();
//            Gson gsonObj = new Gson();
//            Map<?, ?> data = gsonObj.fromJson(response, Map.class);
//            boolean isSuccess = (Boolean) data.get("successs");
//            String answer = "";
//            List<GetIndoorRecommendationRes> resultList = new ArrayList<>();
//
//            if (isSuccess) {
//                answer = (String) data.get("answer");
//
//                // 줄 단위로 나누기
//                String[] lines = answer.split("\n");
//
//                if (lines.length > 1) {
//                    // 첫 줄은 헤더
//                    String[] header = lines[0].split(",");
//
//                    for (int i = 1; i < lines.length; i++) {
//                        String[] values = lines[i].split(",", -1);
//                        if (values.length < 6) continue;
//
//                        GetIndoorRecommendationRes res = new GetIndoorRecommendationRes();
//                        res.setRank(Integer.parseInt(values[0].trim()));
//                        res.setStore(values[1].trim());
//                        res.setCorner(values[2].trim());
//
//                        res.setWaiting_pred(Integer.parseInt(values[4].trim()));
//                        res.setScore(Integer.parseInt(values[5].trim()));
//                        res.setComment(values[6].trim());
//
//                        resultList.add(res);
//                    }
//
//                    return resultList;
//                } else {
//                    throw new BaseException(BaseResponseStatus.INVALID_JWT);
//                }
//            } else {
//                throw new BaseException(BaseResponseStatus.INVALID_JWT);
//            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.INVALID_AUTH_INPUT);
        }

    }

    @Transactional
    public List<GetOutdoorRecommendationRes> getOutdoorRecommendation(Long userId) throws BaseException, URISyntaxException {
        GetUserPreferenceRes getUserPreferenceRes = getUserPreference(userId);
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디 설정
        Map<String, String> body = new HashMap<>();
        body.put("age", getUserPreferenceRes.getAge().toString());
        body.put("gender", getUserPreferenceRes.getGender());
        body.put("weather", getUserPreferenceRes.getWeatherInfo());
        body.put("preferred", getUserPreferenceRes.getFoodPreferred());
        body.put("disliked", getUserPreferenceRes.getFoodDislike());
        body.put("distance", getUserPreferenceRes.getDistance().toString());
        body.put("price_limit", getUserPreferenceRes.getPriceLimit().toString());

        try {
            /* <--------------------- Dummy -------------------> */
            String message = "rank,store,menu,price,score,comment\n1,일편등심 마곡나루점,한우 쌈장찌개,7000,95,맑은 날에 시원하게 즐길 수 있는 메뉴입니다.\n2,무닌 마곡,명란구이,14000,90,세련된 맛의 메뉴로 맑은 날에 적합합니다.\n3,어부몽 코엑스마곡점,[황토가마] 고등어구이 반상,15000,85,맛있는 고등어구이로 선호도가 높을 것입니다.\n4,전일맥주 마곡역점,수제비조개탕,15000,80,맑은 날에도 부드러운 맛을 즐길 수 있는 메뉴입니다.\n5,봄이보리밥 마곡점,황금 고등어구이 반상,15000,75,맑은 날에 즐길 수 있는 정갈한 메뉴입니다.";
            List<GetOutdoorRecommendationRes> resultList = new ArrayList<>();
            // 줄 단위로 나누기
            String[] lines = message.split("\n");
            log.info("lines[0]: {}", lines[0]);
            if (lines.length > 1) {
                // 첫 줄은 헤더
                String[] header = lines[0].split(",");
                log.info("lines: 여기2");
                for (int i = 1; i < lines.length; i++) {
                    String[] values = lines[i].split(",", -1);
                    if (values.length < 6) continue;

                    GetOutdoorRecommendationRes res = new GetOutdoorRecommendationRes();
                    res.setRank(Integer.parseInt(values[0].trim()));
                    res.setStore(values[1].trim());
                    res.setMenu(values[2].trim());
                    res.setPrice(Integer.parseInt(values[3].trim()));
                    res.setScore(Integer.parseInt(values[4].trim()));
                    res.setComment(values[5].trim());

                    resultList.add(res);
                }

                return resultList;
            } else {
                throw new BaseException(BaseResponseStatus.INVALID_JWT);
            }
            /* <--------------------- Dummy -------------------> */

//            RestTemplate restTemplate = new RestTemplate();
//            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
//            ResponseEntity<String> responseEntity = restTemplate.exchange(
//                    Constants.AI_SERVER_URL,
//                    HttpMethod.POST,
//                    requestEntity,
//                    String.class
//            );
//
//            String response = responseEntity.getBody();
//            Gson gsonObj = new Gson();
//            Map<?, ?> data = gsonObj.fromJson(response, Map.class);
//            boolean isSuccess = (Boolean) data.get("successs");
//            List<GetOutdoorRecommendationRes> resultList = new ArrayList<>();
//            // 줄 단위로 나누기
//            String[] lines = message.split("\n");
//
//            if (lines.length > 1) {
//                // 첫 줄은 헤더
//                String[] header = lines[0].split(",");
//
//                for (int i = 1; i < lines.length; i++) {
//                    String[] values = lines[i].split(",", -1);
//                    if (values.length < 6) continue;
//
//                    GetOutdoorRecommendationRes res = new GetOutdoorRecommendationRes();
//                    res.setRank(Integer.parseInt(values[0].trim()));
//                    res.setStore(values[1].trim());
//                    res.setMenu(values[2].trim());
//                    res.setPrice(Integer.parseInt(values[3].trim()));
//                    res.setScore(Integer.parseInt(values[4].trim()));
//                    res.setComment(values[5].trim());
//
//                    resultList.add(res);
//                }
//
//                return resultList;
//            } else {
//                throw new BaseException(BaseResponseStatus.INVALID_JWT);
//            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.INVALID_AUTH_INPUT);
        }

    }

    @Transactional
    private GetUserPreferenceRes getUserPreference(Long userId) throws BaseException, URISyntaxException {
        User user = utilService.findByUserIdWithValidation(userId);
        int age = user.getAge();
        String gender = user.getGender();
        String foodPreferred = user.getFoodPreferred();
        String foodDislike = user.getFoodDislike();
        int priceLimit = user.getPriceLimit();

        boolean isTolerateWaitTime = user.isTolerateWaitTime();
        int waitLimit;
        if(isTolerateWaitTime) {
            waitLimit = WAIT_TIME_TOLERANCE;
        } else {
            waitLimit = WAIT_TIME_INTOLERANCE;
        }

        boolean isLocalPreferred = user.isLocalPreferred();
        int distance;
        if(isLocalPreferred) {
            distance = SHORT_DISTANCE;
        } else {
            distance = LONG_DISTANCE;
        }

        String weatherInfo = getWeatherInfo();

        return new GetUserPreferenceRes(age, gender, foodPreferred, foodDislike, waitLimit, distance, priceLimit, weatherInfo);
    }

    @Transactional
    private String getWeatherInfo() throws URISyntaxException {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        RestTemplate restTemplate = new RestTemplate();

        String serviceKey = URLEncoder.encode(secret.getServiceKey(), StandardCharsets.UTF_8); // 꼭 필요함

        String finalUrl = String.format(
                "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"
                        + "?ServiceKey=%s&pageNo=1&numOfRows=100&dataType=JSON"
                        + "&base_date=%s&base_time=0500&nx=57&ny=126",
                serviceKey, date
        );

        URI uri = new URI(finalUrl);

        try {
            String weatherInfo = restTemplate.getForObject(uri, String.class);
            Gson gsonObj = new Gson();
            Map<?, ?> responseMap = gsonObj.fromJson(weatherInfo, Map.class);

            Map<?, ?> response = (Map<?, ?>) responseMap.get("response");
            Map<?, ?> body = (Map<?, ?>) response.get("body");
            Map<?, ?> items = (Map<?, ?>) body.get("items");
            List<?> itemList = (List<?>) items.get("item");

            Set<String> targetCategories = new HashSet<>(Arrays.asList("sky", "pty", "pop"));
            Map<String, Integer> resultMap = new HashMap<>();

            for (Object obj : itemList) {
                Map<?, ?> item = (Map<?, ?>) obj;
                String category = ((String) item.get("category")).toLowerCase();

                if (targetCategories.contains(category)) {
                    Object fcstValue = item.get("fcstValue");
                    try {
                        int intValue = Integer.parseInt(fcstValue.toString());
                        resultMap.put(category, intValue);
                    } catch (NumberFormatException e) {
                        throw new BaseException(BaseResponseStatus.INVALID_AUTH_INPUT);
                    }
                }
            }
            return gsonObj.toJson(resultMap);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.INVALID_AUTH_INPUT);
        }

    }

}
