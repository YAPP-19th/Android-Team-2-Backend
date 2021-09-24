package com.yapp.sharefood.auth.service;

import com.yapp.sharefood.auth.dto.OAuthDto;
import com.yapp.sharefood.auth.dto.request.AuthRequsetDto;
import com.yapp.sharefood.auth.manager.AuthenticationManager;
import com.yapp.sharefood.external.OAuthProfile;
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

    @Transactional
    public OAuthDto authenticate(AuthRequsetDto authRequsetDto) {
        OAuthProfile profile = authenticationManager.requestOAuthUserInfo(authRequsetDto.getOAuthType(), authRequsetDto.getAccessToken());
        User newUser = User.builder()
                .oauthId(profile.getOauthId())
                .oAuthType(authRequsetDto.getOAuthType())
                .name(profile.oauthNickname())
                .build();
        User saveUser = userRepository.save(newUser);

        return new OAuthDto("");
    }
}
