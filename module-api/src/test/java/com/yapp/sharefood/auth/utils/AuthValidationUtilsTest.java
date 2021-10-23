package com.yapp.sharefood.auth.utils;

import com.yapp.sharefood.common.exception.ForbiddenException;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.yapp.sharefood.auth.utils.AuthValidationUtils.validateUserIdPath;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthValidationUtilsTest {

    @Test
    @DisplayName("회원 정보가 일치할 경우")
    void validateUserIdPathTest() {
        // given
        Long userId = 1L;
        User authUser = User.builder()
                .id(1L)
                .oAuthType(OAuthType.KAKAO)
                .nickname("nickname")
                .name("name")
                .oauthId("oauth-id")
                .build();

        // when
        validateUserIdPath(userId, authUser);

        // then
    }

    @Test
    @DisplayName("path id 가 null일 경우")
    void validateUserIdPathWhenPathIdIsNullTest() throws Exception {
        Long userId = null;
        User authUser = User.builder()
                .id(1L)
                .oAuthType(OAuthType.KAKAO)
                .nickname("nickname")
                .name("name")
                .oauthId("oauth-id")
                .build();

        // when

        // then
        assertThrows(ForbiddenException.class, () -> validateUserIdPath(userId, authUser));
    }

    @Test
    @DisplayName("user가 null일 경우 테스트")
    void validateUserIdPathWhenUserNullTest() throws Exception {
        // given
        Long userId = 1L;
        User authUser = null;

        // when

        // then
        assertThrows(ForbiddenException.class, () -> validateUserIdPath(userId, authUser));
    }

    @Test
    @DisplayName("user가 id가 null일 경우 테스트")
    void validateUserIdPathWhenUserIdNullTest() throws Exception {
        // given
        Long userId = null;
        User authUser = User.builder()
                .oAuthType(OAuthType.KAKAO)
                .nickname("nickname")
                .name("name")
                .oauthId("oauth-id")
                .build();

        // when

        // then
        assertThrows(ForbiddenException.class, () -> validateUserIdPath(userId, authUser));
    }
}