package com.yapp.sharefood.auth.utils;

import com.yapp.sharefood.common.exception.ForbiddenException;
import com.yapp.sharefood.user.domain.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthValidationUtils {
    public static void validateUserIdPath(Long pathUserId, User tokenUser) {
        if (!(Objects.nonNull(pathUserId) && Objects.nonNull(tokenUser) && Objects.nonNull(tokenUser.getId()) && Objects.equals(tokenUser.getId(), pathUserId))) {
            throw new ForbiddenException();
        }
    }
}
