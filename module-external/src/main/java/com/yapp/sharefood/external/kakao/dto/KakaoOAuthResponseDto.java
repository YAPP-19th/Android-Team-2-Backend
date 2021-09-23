package com.yapp.sharefood.external.kakao.dto;

import com.yapp.sharefood.external.OAuthResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoOAuthResponseDto implements OAuthResponseDto {
    private String id;

    public KakaoOAuthResponseDto(String id) {
        this.id = id;
    }
}
