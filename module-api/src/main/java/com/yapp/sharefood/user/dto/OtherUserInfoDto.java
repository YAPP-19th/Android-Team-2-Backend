package com.yapp.sharefood.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OtherUserInfoDto {
    private Long id;
    private String nickname;

    public static OtherUserInfoDto of(Long id, String nickname) {
        return new OtherUserInfoDto(id, nickname);
    }
}
