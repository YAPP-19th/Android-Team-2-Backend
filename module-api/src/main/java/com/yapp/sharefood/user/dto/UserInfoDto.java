package com.yapp.sharefood.user.dto;

import com.yapp.sharefood.user.domain.Grade;
import com.yapp.sharefood.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Data
public class UserInfoDto {
    private Long id;
    private String nickname;
    private Grade grade;
    private Integer userGradePoint;

    public static UserInfoDto of(User user) {
        return new UserInfoDto(user.getId(), user.getNickname(), user.getGrade(), user.getGradePoint());
    }

    public static UserInfoDto of(Long id, String nickname, Grade grade, Integer userGradePoint) {
        return new UserInfoDto(id, nickname, grade, userGradePoint);
    }
}
