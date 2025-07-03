package com.example.recommend_lunch_menu.utils;

import com.example.recommend_lunch_menu.exception.BaseException;
import com.example.recommend_lunch_menu.user.User;
import com.example.recommend_lunch_menu.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.recommend_lunch_menu.exception.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class UtilService {

    private final UserRepository userRepository;

    public User findByUserIdWithValidation(Long userId) throws BaseException {
        return userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new BaseException(NONE_EXIST_USER));
    }

    public User findByEmailWithValidation(String email) throws BaseException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(DATABASE_ERROR));
    }

    public String findAccessTokenByUserIdWithValidation(Long userId) throws BaseException {
        return userRepository.findAccessTokenByUserId(userId)
                .orElseThrow(() -> new BaseException(POST_USERS_NONE_EXISTS_ID));
    }

    public User findAdminUserWithValidation() throws BaseException {
        return userRepository.findAdminUser()
                .orElseThrow(() -> new BaseException(ADMIN_USERS_NONE_EXISTS));
    }

    public String findAccessTokenForAdminWithValidation() throws BaseException {
        return userRepository.findAccessTokenForAdmin()
                .orElseThrow(() -> new BaseException(ADMIN_USERS_NONE_EXISTS));
    }

}
