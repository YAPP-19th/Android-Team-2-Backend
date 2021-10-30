package com.yapp.sharefood.auth.utils;

import com.yapp.sharefood.common.exception.ForbiddenException;
import com.yapp.sharefood.user.domain.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthUtils {
    private static final String AUTH_TOKEN_HEADER = "Authorization";

    public static void validateUserIdPath(Long pathUserId, User tokenUser) {
        if (!(Objects.nonNull(pathUserId) && Objects.nonNull(tokenUser) && Objects.nonNull(tokenUser.getId()) && Objects.equals(tokenUser.getId(), pathUserId))) {
            throw new ForbiddenException();
        }
    }

    public static String extractToken(HttpServletRequest request) {
        return request.getHeader(AUTH_TOKEN_HEADER);
    }

    public static String extractTokenByWebRequset(NativeWebRequest webRequest) {
        return webRequest.getHeader(AUTH_TOKEN_HEADER);
    }

    public static void setTokenInHeader(HttpServletResponse response, String token) {
        response.setHeader(AUTH_TOKEN_HEADER, token);
    }
}
