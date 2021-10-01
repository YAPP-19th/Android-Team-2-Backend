package com.yapp.sharefood.user.service;

import com.yapp.sharefood.common.random.RandomStringCreator;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.exception.UserNicknameExistException;
import com.yapp.sharefood.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.willReturn;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @MockBean
    RandomStringCreator randomStringCreator;

    @Test
    @DisplayName("유니크한 nickname 생성")
    void uniqueNicknameCreatTest() {
        // given
        willReturn("randomName")
                .given(randomStringCreator).createRandomUUIDStr();

        // when
        String uniqueNickname = userService.createUniqueNickname();

        // then
        assertEquals("냠냠학사 randomName", uniqueNickname);
    }

    @Test
    @DisplayName("이미 존재하는 nickname을 반환")
    void nonUniqueNicknameCreatTest() {
        // given
        User user = User.builder()
                .nickname("냠냠학사 randomName")
                .oauthId("kaka_id")
                .oAuthType(OAuthType.KAKAO)
                .name("kkh")
                .build();
        userRepository.save(user);
        willReturn("randomName")
                .given(randomStringCreator).createRandomUUIDStr();

        // when

        // then
        assertThrows(UserNicknameExistException.class, () -> userService.createUniqueNickname());
    }
}