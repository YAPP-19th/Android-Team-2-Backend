package com.yapp.sharefood.common;

import com.yapp.sharefood.auth.token.TokenProvider;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willReturn;

public abstract class PreprocessController {

    @MockBean
    protected UserRepository userRepository;
    @MockBean
    protected TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .nickname("nickname")
                .oauthId("oauth_id")
                .oAuthType(OAuthType.KAKAO)
                .name("name")
                .build();

        willReturn(true)
                .given(tokenProvider).isValidToken(anyString());
        willReturn(1L)
                .given(tokenProvider).extractIdByToken(anyString());

        willReturn(Optional.of(user))
                .given(userRepository).findById(anyLong());
    }
}
