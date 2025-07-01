package com.example.recommend_lunch_menu.sandi;

import com.example.recommend_lunch_menu.exception.BaseException;
import com.example.recommend_lunch_menu.exception.BaseResponseStatus;
import com.example.recommend_lunch_menu.scheduler.dto.LoginInfo;
import com.example.recommend_lunch_menu.scheduler.store.WeekDayTableStore;
import com.example.recommend_lunch_menu.scheduler.store.TokenStore;
import com.example.recommend_lunch_menu.utils.Constants;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class SandiService {

    private final TokenStore tokenStore;
    private final WeekDayTableStore weekDayTableStore;
    private final LoginInfo loginInfo;

    @Transactional
    public String issueAccessToken() throws BaseException {
        String username = loginInfo.getUsername();
        String password = loginInfo.getPassword();
        String clientId = loginInfo.getClientId();

        RestTemplate restTemplate = new RestTemplate();
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디 설정
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("clientId", clientId);
        String accessToken = "";

        try {
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    "https://lifeapp-api.sandimall.com/api/oauth2/token",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            String tokenInfo = responseEntity.getBody();
            Gson gsonObj = new Gson();
            Map<?, ?> data = gsonObj.fromJson(tokenInfo, Map.class);
            accessToken = (String) data.get("accessToken");

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.INVALID_AUTH_INPUT);
        }

        tokenStore.setAccessToken("Bearer " + accessToken);
        log.info("AccessToken Info: {}", tokenStore.getAccessToken());

        return accessToken;
    }

    @Transactional
    public String getOurHomeWeekdayTable() throws BaseException {
        int postId = getBoardList(StoreType.OUR_HOME);
        String imgUrl = getImgUrl(postId);
        weekDayTableStore.setOurHomeTableImgUrl(imgUrl);
        log.info("Image URL: {}", weekDayTableStore.getOurHomeTableImgUrl());
        return imgUrl;
    }

    @Transactional
    public String getCjFreshWeekdayTable() throws BaseException {
        int postId = getBoardList(StoreType.CJ_FRESH);
        String imgUrl = getImgUrl(postId);
        weekDayTableStore.setCjFreshTableImgUrl(imgUrl);
        log.info("Image URL: {}", weekDayTableStore.getCjFreshTableImgUrl());
        return imgUrl;
    }

    @Transactional
    public String getPulmuoneWeekdayTable() throws BaseException {
        int postId = getBoardList(StoreType.PULMUONE);
        String imgUrl = getImgUrl(postId);
        weekDayTableStore.setPulmuoneTableImgUrl(imgUrl);
        log.info("Image URL: {}", weekDayTableStore.getPulmuoneTableImgUrl());
        return imgUrl;
    }

    private String getImgUrl(Integer postId) {
        String accessToken = tokenStore.getAccessToken();
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = createCommonHeaders(accessToken);

        try {
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    "https://lifeapp-api.sandimall.com/api/v2/app/pinelife/post/" + postId,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            String tableInfo = responseEntity.getBody();
            Gson gsonObj = new Gson();
            Map<?, ?> responseMap = gsonObj.fromJson(tableInfo, Map.class);
            Map<?, ?> fileInfo = (Map<?, ?>) responseMap.get("fileInfo");
            List<Map<String, Object>> items = (List<Map<String, Object>>) fileInfo.get("items");

            String imgUrl = null;
            for (Map<String, Object> item : items) {
                String scale = (String) item.get("scale");
                if ("full".equals(scale)) {
                    imgUrl = (String) item.get("url");
                    break;
                }
            }

            return imgUrl;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.INVALID_AUTH_INPUT);
        }
    }

    @Transactional
    private Integer getBoardList(StoreType storeType) throws BaseException {
        String accessToken = tokenStore.getAccessToken();
        RestTemplate restTemplate = new RestTemplate();

        String storeId;
        if (storeType == StoreType.OUR_HOME) {
            storeId = Constants.SANDI_OURHOME_SPACE_ID;
        } else if (storeType == StoreType.CJ_FRESH) {
            storeId = Constants.SANDI_CJFRESH_SPACE_ID;
        } else {
            storeId = Constants.SANDI_PULMUONE_SPACE_ID;
        }

        // 헤더 설정
        HttpHeaders headers = createCommonHeaders(accessToken);

        // 파라미터 설정
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("category", "building-menu");
        queryParams.add("sort", "createdAt.desc");
        queryParams.add("page", "1");
        queryParams.add("pageSize", "10");
        queryParams.add("storeId", storeId);
        queryParams.add("buildingId", Constants.SANDI_BUILDING_ID);

        String url = "https://lifeapp-api.sandimall.com/api/v2/app/pinelife/post/list";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);
        String finalUrl = builder.toUriString();

        int postId = 0;
        try {
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    finalUrl,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            String boardInfo = responseEntity.getBody();
            Gson gsonObj = new Gson();
            Map<?, ?> responseMap = gsonObj.fromJson(boardInfo, Map.class);
            List<Map<String, Object>> items = (List<Map<String, Object>>) responseMap.get("items");

            for (Map<String, Object> item : items) {
                String title = (String) item.get("title");
                if (title != null && title.contains("주간")) {
                    Double postIdDouble = (Double) item.get("lifePostId");
                    postId = postIdDouble.intValue();
                    break;
                }
            }

            log.info("Post ID: {}", postId);
            return postId;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.INVALID_AUTH_INPUT);
        }
    }

    private static HttpHeaders createCommonHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", "application/json, text/plain, */*");
        headers.add("Accept-Encoding", "gzip, deflate, br, zstd");
        headers.add("Accept-Language", "ko-KR,ko:;q=0.9,en-US;q=0.8,en;q=0.7");
        headers.add("Origin", "https://lifeapp.sandimall.com");
        headers.add("Referer", "https://lifeapp.sandimall.com/");
        headers.add("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36");
        headers.add("authorization", accessToken);

        return headers;
    }
}
