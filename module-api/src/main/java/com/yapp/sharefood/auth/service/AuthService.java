package com.yapp.sharefood.auth.service;

import com.yapp.sharefood.auth.dto.OAuthDto;
import com.yapp.sharefood.auth.dto.request.AuthCreationRequestDto;
import com.yapp.sharefood.auth.dto.request.AuthRequestDto;
import com.yapp.sharefood.auth.manager.AuthenticationManager;
import com.yapp.sharefood.auth.token.TokenProvider;
import com.yapp.sharefood.external.OAuthProfile;
import com.yapp.sharefood.oauth.exception.OAUthExistException;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public OAuthDto authenticate(AuthRequestDto authRequestDto) {
        OAuthProfile profile = authenticationManager.requestOAuthUserInfo(authRequestDto.getOauthType(), authRequestDto.getAccessToken());
        User existUser = userRepository.findByOAuthIdAndOAuthType(profile.getOauthId(), authRequestDto.getOauthType())
                .orElseThrow(UserNotFoundException::new);

        String token = tokenProvider.createToken(existUser);

        return OAuthDto.of(existUser.getId(), token, authRequestDto.getOauthType());
    }

    @Transactional
    public OAuthDto singUp(AuthCreationRequestDto creationRequestDto) {
        OAuthProfile profile = authenticationManager.requestOAuthUserInfo(creationRequestDto.getOauthType(), creationRequestDto.getAccessToken());
        // check redis logic
        Optional<User> optionalExistUser = userRepository.findByOAuthIdAndOAuthType(profile.getOauthId(), creationRequestDto.getOauthType());

        if (optionalExistUser.isPresent()) {
            throw new OAUthExistException(String.format("%s로 이미 등록된 회원입니다.", creationRequestDto.getOauthType().name()));
        }

        User newUser = User.builder()
                .nickname(creationRequestDto.getNickname())
                .oauthId(profile.getOauthId())
                .name(profile.oauthNickname())
                .oAuthType(creationRequestDto.getOauthType())
                .build();
        User saveUser = userRepository.save(newUser);
        String token = tokenProvider.createToken(saveUser);

        return OAuthDto.of(saveUser.getId(), token, creationRequestDto.getOauthType());
    }
}
