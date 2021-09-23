package com.yapp.sharefood.external.kakao;

import com.yapp.sharefood.external.AuthProvider;
import com.yapp.sharefood.external.OAuthProfile;
import com.yapp.sharefood.external.exception.BadGatewayException;
import com.yapp.sharefood.external.kakao.dto.KakaoOAuthProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class KakaoAuthProvider implements AuthProvider {
    private final WebClient webClient;

    @Value("${oauth.kakao.profile-url}")
    private String kakaoOAuthUrl;

    @Override
    public OAuthProfile getOAuthProfileInfo(String accessToken) {
        return webClient.post()
                .uri(kakaoOAuthUrl)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new BadGatewayException(String.format("잘못된 kakao 토큰 (%s) 입니다", accessToken))))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new BadGatewayException("kakao 로그인 연동 중 에러가 발생하였습니다")))
                .bodyToMono(KakaoOAuthProfile.class)
                .block();
    }
}
