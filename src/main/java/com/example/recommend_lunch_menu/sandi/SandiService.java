package com.example.recommend_lunch_menu.sandi;

import com.example.recommend_lunch_menu.exception.BaseException;
import com.example.recommend_lunch_menu.exception.BaseResponseStatus;
import com.example.recommend_lunch_menu.scheduler.store.AdminInfo;
import com.example.recommend_lunch_menu.scheduler.store.DailyMenuStore;
import com.example.recommend_lunch_menu.scheduler.store.WeekDayTableStore;
import com.example.recommend_lunch_menu.token.JwtProvider;
import com.example.recommend_lunch_menu.token.dto.JwtResponseDto;
import com.example.recommend_lunch_menu.user.User;
import com.example.recommend_lunch_menu.user.UserRepository;
import com.example.recommend_lunch_menu.user.dto.PostUserReq;
import com.example.recommend_lunch_menu.utils.AES128;
import com.example.recommend_lunch_menu.utils.Constants;
import com.example.recommend_lunch_menu.utils.Secret;
import com.example.recommend_lunch_menu.utils.UtilService;
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

import java.util.*;

import static com.example.recommend_lunch_menu.exception.BaseResponseStatus.*;
import static com.example.recommend_lunch_menu.utils.ValidationRegEx.isRegExEmail;

@RequiredArgsConstructor
@Service
@Slf4j
public class SandiService {

    private final WeekDayTableStore weekDayTableStore;
    private final DailyMenuStore dailyMenuStore;
    private final UserRepository userRepository;
    private final UtilService utilService;
    private final Secret secret;
    private final AdminInfo adminInfo;
    private final JwtProvider jwtProvider;

    @Transactional
    public String verifyUser(PostUserReq postUserReq) throws BaseException {
        String email = postUserReq.getEmail();
        String password = postUserReq.getPassword();

        if(!isRegExEmail(email)) throw new BaseException(POST_USERS_INVALID_EMAIL);

        try{
            password = new AES128(secret.getAesKey()).encrypt(password); // 암호화 코드
        }
        catch (Exception e) {
            log.error("Error Occurred: {}", e.getMessage());
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        String accessToken = issueAccessToken(email, password);
        User user;
        if (userRepository.findByEmailCount(email) < 1) {
            user = User.builder()
                    .email(email)
                    .password(password)
                    .accessToken(accessToken)
                    .build();

            if (user.getEmail().equals(adminInfo.getUsername())) {
                user.setAdmin(true);
            }

            userRepository.save(user);
        } else {
            user = utilService.findByEmailWithValidation(email);
        }

        JwtResponseDto.TokenInfo tokenInfo = jwtProvider.generateToken(user.getUserId());
        return tokenInfo.getAccessToken();
    }

    @Transactional
    public void updateAccessTokenForAdmin() throws BaseException {
        String email = adminInfo.getUsername();
        String password = adminInfo.getPassword();
        String accessToken = issueAccessToken(email, password);
        User user = utilService.findByEmailWithValidation(email);
        user.setAccessToken(accessToken);
    }

    @Transactional
    public String issueAccessToken(String email, String password) throws BaseException {
        try {
            password = new AES128(secret.getAesKey()).decrypt(password);
        } catch (Exception e) {
            log.error("Error Occurred: {}", e.getMessage());
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        RestTemplate restTemplate = new RestTemplate();
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디 설정
        Map<String, String> body = new HashMap<>();
        body.put("username", email);
        body.put("password", password);
        body.put("clientId", "MOBILE");
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

        log.info("AccessToken Info: {}", accessToken);

        return "Bearer " + accessToken;
    }

    @Transactional
    public String getOurHomeWeekdayTable() throws BaseException {
        String accessToken = getAccessTokenForAdmin();
        int postId = getBoardList(accessToken, StoreType.OUR_HOME, "주간");
        List<String> imgUrls = getImageUrlsFromPost(accessToken, postId);
        String imgUrl = imgUrls.isEmpty() ? null : imgUrls.get(0);
        weekDayTableStore.setOurHomeTableImgUrl(imgUrl);
        log.info("Image URL: {}", weekDayTableStore.getOurHomeTableImgUrl());
        return imgUrl;
    }

    @Transactional
    public String getCjFreshWeekdayTable() throws BaseException {
        String accessToken = getAccessTokenForAdmin();
        int postId = getBoardList(accessToken, StoreType.CJ_FRESH, "주간");
        List<String> imgUrls = getImageUrlsFromPost(accessToken, postId);
        String imgUrl = imgUrls.isEmpty() ? null : imgUrls.get(0);
        weekDayTableStore.setCjFreshTableImgUrl(imgUrl);
        log.info("Image URL: {}", weekDayTableStore.getCjFreshTableImgUrl());
        return imgUrl;
    }

    @Transactional
    public String getPulmuoneWeekdayTable() throws BaseException {
        String accessToken = getAccessTokenForAdmin();
        int postId = getBoardList(accessToken, StoreType.PULMUONE, "주간");
        List<String> imgUrls = getImageUrlsFromPost(accessToken, postId);
        String imgUrl = imgUrls.isEmpty() ? null : imgUrls.get(0);
        weekDayTableStore.setPulmuoneTableImgUrl(imgUrl);
        log.info("Image URL: {}", weekDayTableStore.getPulmuoneTableImgUrl());
        return imgUrl;
    }

    @Transactional
    public List<String> getOurHomeDailyMenu() throws BaseException {
        String accessToken = getAccessTokenForAdmin();
        int postId = getBoardList(accessToken, StoreType.OUR_HOME, "오늘의");
        List<String> imgUrls = getImageUrlsFromPost(accessToken, postId);
        dailyMenuStore.setOurHomeImgUrls(imgUrls);
        log.info("Image URL: {}", dailyMenuStore.getOurHomeImgUrls());
        return imgUrls;
    }

    @Transactional
    public List<String> getCjFreshDailyMenu() throws BaseException {
        String accessToken = getAccessTokenForAdmin();
        int postId = getBoardList(accessToken, StoreType.CJ_FRESH, "오늘의");
        List<String> imgUrls = getImageUrlsFromPost(accessToken, postId);
        dailyMenuStore.setCjFreshImgUrls(imgUrls);
        log.info("Image URL: {}", dailyMenuStore.getCjFreshImgUrls());
        return imgUrls;
    }

    private List<String> getImageUrlsFromPost(String accessToken, Integer postId) {
        RestTemplate restTemplate = new RestTemplate();
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
            Gson gson = new Gson();
            Map<?, ?> responseMap = gson.fromJson(tableInfo, Map.class);
            Map<?, ?> fileInfo = (Map<?, ?>) responseMap.get("fileInfo");

            if (fileInfo == null || !fileInfo.containsKey("items")) {
                return Collections.emptyList();
            }

            List<Map<String, Object>> items = (List<Map<String, Object>>) fileInfo.get("items");
            List<String> imgUrls = new ArrayList<>();

            for (Map<String, Object> item : items) {
                String scale = (String) item.get("scale");
                if ("full".equals(scale)) {
                    imgUrls.add((String) item.get("url"));
                }
            }

            return imgUrls;

        } catch (Exception e) {
            log.error("Error fetching post image URLs: {}", e.getMessage());
            throw new BaseException(BaseResponseStatus.INVALID_AUTH_INPUT);
        }
    }

    @Transactional
    private Integer getBoardList(String accessToken, StoreType storeType, String keyword) throws BaseException {
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
                if (title != null && title.contains(keyword)) {
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

    private String getAccessTokenForAdmin() {
        return utilService.findAccessTokenForAdminWithValidation();
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
