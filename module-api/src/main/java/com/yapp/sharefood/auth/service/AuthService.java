package com.yapp.sharefood.auth.service;

import com.yapp.sharefood.auth.dto.request.AuthRequsetDto;
import com.yapp.sharefood.auth.manager.AuthenticationManager;
import com.yapp.sharefood.external.OAuthProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final AuthenticationManager authenticationManager;

    @Transactional
    public OAuthProfile authenticate(AuthRequsetDto authRequsetDto) {
        OAuthProfile profile = authenticationManager.requestOAuthUserInfo(authRequsetDto.getOAuthType(), authRequsetDto.getAccessToken());
        // add token
        return profile;
    }
}
