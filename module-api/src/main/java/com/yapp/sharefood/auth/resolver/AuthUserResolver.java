package com.yapp.sharefood.auth.resolver;

import com.yapp.sharefood.auth.token.TokenProvider;
import com.yapp.sharefood.auth.utils.AuthUtils;
import com.yapp.sharefood.oauth.exception.AuthHeaderOmittedException;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthUserResolver implements HandlerMethodArgumentResolver {

    private final HttpServletRequest httpServletRequest;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.getParameterAnnotation(AuthUser.class) != null;
        boolean isMatchType = parameter.getParameterType().equals(User.class);

        if (hasAnnotation && AuthUtils.extractToken(httpServletRequest) == null) {
            throw new AuthHeaderOmittedException();
        }

        return hasAnnotation && isMatchType;
    }

    @Override
    public User resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String authToken = AuthUtils.extractTokenByWebRequset(webRequest);

        if (tokenProvider.isValidToken(authToken)) {
            Long userId = tokenProvider.extractIdByToken(authToken);
            return userRepository.findById(userId)
                    .orElseThrow(UserNotFoundException::new);
        }

        return null;
    }
}
