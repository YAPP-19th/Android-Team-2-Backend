package com.yapp.sharefood.auth.interceptor;

import com.yapp.sharefood.auth.token.TokenProvider;
import com.yapp.sharefood.auth.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final TokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        validateAccessToken(request);

        return true;
    }

    private void validateAccessToken(HttpServletRequest request) {
        String accessToken = AuthUtils.extractToken(request);
        tokenProvider.isValidToken(accessToken);
    }
}
