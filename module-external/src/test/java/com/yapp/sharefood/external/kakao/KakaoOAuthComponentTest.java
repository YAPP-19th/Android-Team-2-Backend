package com.yapp.sharefood.external.kakao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class KakaoOAuthComponentTest {
    @Autowired
    KakaoOAuthComponent kakaoOAuthComponent;


    @Test
    void kakaoApiUrlCheck() throws Exception {
        // given

        // when

        // then
        assertEquals("https://kapi.kakao.com/v2/user/me", kakaoOAuthComponent.oauthUrl());
    }
}