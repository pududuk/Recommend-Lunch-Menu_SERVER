package com.example.recommend_lunch_menu.menu;

import com.example.recommend_lunch_menu.exception.BaseException;
import com.example.recommend_lunch_menu.exception.BaseResponseStatus;
import com.example.recommend_lunch_menu.menu.dto.GetOcrServerRes;
import com.example.recommend_lunch_menu.sandi.StoreType;
import com.example.recommend_lunch_menu.schedule.dto.GetS3Res;
import com.example.recommend_lunch_menu.schedule.store.DailyMenuStore;
import com.example.recommend_lunch_menu.token.dto.JwtResponseDto;
import com.example.recommend_lunch_menu.user.User;
import com.example.recommend_lunch_menu.user.dto.GetIndoorRecommendationRes;
import com.example.recommend_lunch_menu.user.dto.PostUserReq;
import com.example.recommend_lunch_menu.utils.AES128;
import com.example.recommend_lunch_menu.utils.Constants;
import com.example.recommend_lunch_menu.utils.S3Service;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

import static com.example.recommend_lunch_menu.exception.BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR;
import static com.example.recommend_lunch_menu.exception.BaseResponseStatus.POST_USERS_INVALID_EMAIL;
import static com.example.recommend_lunch_menu.utils.ValidationRegEx.isRegExEmail;

@RequiredArgsConstructor
@Service
@Slf4j
public class MenuService {

    private final S3Service s3Service;
    private final DailyMenuStore dailyMenuStore;
    private final MenuRepository menuRepository;

    public void requestToOcrServer(StoreType storeType) {
        RestTemplate restTemplate = new RestTemplate();
        List<String> menuUrls;
        String endPoint = "";
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        log.info("OCR Server Request");

        if (storeType == StoreType.OUR_HOME) {
            menuUrls = dailyMenuStore.getOurHomeImgUrls();
            endPoint = "/ourhome";
        } else { // CJ Fresh
            menuUrls = dailyMenuStore.getCjFreshImgUrls();
            endPoint = "/cjfresh";
        }

        queryParams.add("menu_url_1", menuUrls.get(0));
        queryParams.add("menu_url_2", menuUrls.get(1));
        queryParams.add("menu_url_3", menuUrls.get(2));
        queryParams.add("menu_url_4", menuUrls.get(3));

        if (storeType == StoreType.CJ_FRESH) {
            queryParams.add("menu_url_5", menuUrls.get(4));
            queryParams.add("menu_url_6", menuUrls.get(5));
        }

        String url = Constants.OCR_SERVER_URL + endPoint;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);
        String finalUrl = builder.toUriString();

//        try {
//            HttpEntity<Void> requestEntity = new HttpEntity<>(null);
//            restTemplate.exchange(
//                    finalUrl,
//                    HttpMethod.GET,
//                    requestEntity,
//                    Void.class // 응답을 사용하지 않음
//            );
//            log.info("OCR 서버에 요청만 전송 완료");
//        } catch (Exception e) {
//            log.warn("OCR 서버 요청 중 오류 발생: {}", e.getMessage());
//        }
    }

    @Transactional
    public String processOcrResponse(GetOcrServerRes getOcrServerRes) throws BaseException {
        List<MultipartFile> images = getOcrServerRes.getImages();
        List<String> corners = getOcrServerRes.getCorners();
        List<String> names = getOcrServerRes.getNames();

        List<GetS3Res> getS3ResList = s3Service.uploadFiles(images);
        List<Menu> menus = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            char firstChar = corners.toString().charAt(0);
            StoreType storeType = StoreType.CJ_FRESH;
            if (firstChar >= 'A' && firstChar <= 'C') {
                storeType = StoreType.OUR_HOME;
            }

            Menu menu = Menu.builder()
                    .menuName(names.get(i))
                    .fileName(getS3ResList.get(i).getFileName())
                    .imgUrl(getS3ResList.get(i).getImgUrl())
                    .corner(corners.get(i))
                    .storeType(storeType)
                    .build();
            menus.add(menu);
        }

        menuRepository.saveAll(menus);
        uploadToGptLinkedServer(menus);

        return "이미지 처리 작업이 완료되었습니다.";
    }

    @Transactional
    private void uploadToGptLinkedServer(List<Menu> menus) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // 요청 바디 설정
        List<Map<String, String>> menuList = new ArrayList<>();
        for (Menu m : menus) {
            Map<String, String> map = new HashMap<>();
            map.put("corner", m.getCorner());
            map.put("menu", m.getMenuName());
            map.put("store", m.getStoreType().toString());
            menuList.add(map);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("menus", menuList);

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    Constants.GPT_LINKED_SERVER_URL + "/indoor/upload",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            String response = responseEntity.getBody();
            Gson gsonObj = new Gson();
            Map<?, ?> data = gsonObj.fromJson(response, Map.class);
            boolean isSuccess = (Boolean) data.get("successs");
            List<GetIndoorRecommendationRes> resultList = new ArrayList<>();

            if (isSuccess) {

                log.info("Upload to GPT Linked Server Successfully Completed!");
            } else {
                String message = (String) data.get("message");
                log.error("Error Occurred: {}", message);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.INVALID_AUTH_INPUT);
        }
    }
}
