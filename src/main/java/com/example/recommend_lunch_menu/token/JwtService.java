package com.example.recommend_lunch_menu.token;

import com.example.recommend_lunch_menu.exception.BaseException;
import com.example.recommend_lunch_menu.exception.BaseResponseStatus;
import com.example.recommend_lunch_menu.user.User;
import com.example.recommend_lunch_menu.user.UserRepository;
import com.example.recommend_lunch_menu.utils.Constants;
import com.example.recommend_lunch_menu.utils.UtilService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Key;

import static com.example.recommend_lunch_menu.exception.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class JwtService {
    private Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(Constants.JWT_ENCODING_KEY));
    private final JwtProvider jwtProvider;
    private final UtilService utilService;
    private final UserRepository userRepository;

    /**
     * Header에서 Authorization 으로 JWT 추출
     */
    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String authorizationHeader = request.getHeader("Authorization");
        return authorizationHeader.split(" ")[1];
    }

    /**
     * JWT에서 userId 추출
     */
    public Long getUserIdx() throws BaseException {
        // 1. JWT 추출
        String accessToken = getJwt();
        if (accessToken == null || accessToken.isEmpty()) {
            throw new BaseException(EMPTY_JWT);
        }

        try {
            // 2. JWT parsing
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);
            // 3. userId 추출
            Long userId = claims.getBody().get("userId", Long.class);
            utilService.findByUserIdWithValidation(userId);

            return userId;
        } catch (ExpiredJwtException e) {
            throw new BaseException(EXPIRED_USER_JWT);
        } catch (Exception e) {
            throw new BaseException(INVALID_JWT);
        }
    }
}

