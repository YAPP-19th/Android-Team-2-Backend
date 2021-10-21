package com.yapp.sharefood.user.dto;

import com.yapp.sharefood.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfoDto {
    private Long id;
    private String nickname;

    public static UserInfoDto of(User user) {
        return new UserInfoDto(user.getId(), user.getNickname());
    }

    public static UserInfoDto of(Long id, String nickname) {
        return new UserInfoDto(id, nickname);
    }
}
