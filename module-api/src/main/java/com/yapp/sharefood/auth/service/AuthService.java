package com.yapp.sharefood.auth.service;

import com.yapp.sharefood.auth.dto.OAuthDto;
import com.yapp.sharefood.auth.dto.request.AuthRequsetDto;
import com.yapp.sharefood.auth.manager.AuthenticationManager;
import com.yapp.sharefood.auth.token.TokenProvider;
import com.yapp.sharefood.external.OAuthProfile;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public OAuthDto authenticate(AuthRequsetDto authRequsetDto) {
        OAuthProfile profile = authenticationManager.requestOAuthUserInfo(authRequsetDto.getOauthType(), authRequsetDto.getAccessToken());
        User existUser = userRepository.findByOAuthIdAndOAuthType(profile.getOauthId(), authRequsetDto.getOauthType())
                .orElseThrow(UserNotFoundException::new);

        String token = tokenProvider.createToken(existUser);

        return OAuthDto.of(existUser.getId(), token, authRequsetDto.getOauthType());
    }
}
