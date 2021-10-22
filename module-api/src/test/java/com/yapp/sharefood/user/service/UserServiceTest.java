package com.yapp.sharefood.user.service;

import com.yapp.sharefood.common.random.RandomStringCreator;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.dto.UserInfoDto;
import com.yapp.sharefood.user.dto.request.UserNicknameRequest;
import com.yapp.sharefood.user.exception.UserNicknameExistException;
import com.yapp.sharefood.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.willReturn;

@SpringBootTest
@Transactional
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

    @Test
    @DisplayName("중복되는 닉네임이 없는 경우")
    void nicknameNotDuplicateTest() {
        //given
        String nickname = "1";
        UserNicknameRequest request = new UserNicknameRequest(nickname);

        //when

        //then
        userService.checkNicknameDuplicate(request);
    }

    @Test
    @DisplayName("중복되는 닉네임이 존재하는 경우")
    void nicknameDuplicateTest() {
        //given
        String nickname = "donghwan";
        User user = User.builder()
                .oauthId("kaka-id")
                .oAuthType(OAuthType.KAKAO)
                .nickname(nickname)
                .name("kim-dong-hwan")
                .build();
        userRepository.save(user);

        UserNicknameRequest request = new UserNicknameRequest(nickname);

        //when

        //then
        assertThrows(UserNicknameExistException.class, () -> userService.checkNicknameDuplicate(request));
    }

    @Test
    @DisplayName("닉네임 변경 성공")
    void changeNicknameTest() {
        String oldNickname = "donghwan";
        String newNickname = "kimdonghwan";

        User user = User.builder()
                .oauthId("kaka-id")
                .oAuthType(OAuthType.KAKAO)
                .nickname(oldNickname)
                .name("kim-dong-hwan")
                .build();

        UserNicknameRequest request = new UserNicknameRequest(newNickname);

        userRepository.save(user);

        //when
        String resultNickname = userService.changeUserNickname(user.getId(), request);

        //then
        assertEquals(newNickname, resultNickname);
    }

    @Test
    @DisplayName("중복되는 닉네임 존재하는 경우")
    void nicknameNotChangeCauseDuplicateTest() {
        String oldNickname = "donghwan";

        User user = User.builder()
                .oauthId("kaka-id")
                .oAuthType(OAuthType.KAKAO)
                .nickname(oldNickname)
                .name("kim-dong-hwan")
                .build();

        UserNicknameRequest request = new UserNicknameRequest(oldNickname);

        userRepository.save(user);
        Long userId = user.getId();
        //when

        //then
        assertNotNull(userId);
        assertThrows(UserNicknameExistException.class, () -> userService.changeUserNickname(userId, request));
    }

    @Test
    @DisplayName("닉네임을 변경할 유저가 존재하지 않는 경우")
    void nicknameNotChangeCauseUserNotFoundTest() {
        //given
        String nickname = " donghwan";

        UserNicknameRequest request = new UserNicknameRequest(nickname);

        //when

        //then
        assertThrows(UserNotFoundException.class, () -> userService.changeUserNickname(0L, request));
    }

    @Test
    @DisplayName("유저의 정보 조회 성공")
    void findUserTest() {
        //given
        User user = User.builder()
                .name("donghwan")
                .nickname("donghwan")
                .oauthId("kakao-id")
                .oAuthType(OAuthType.KAKAO)
                .build();

        userRepository.save(user);

        //when
        UserInfoDto resultUserDto = userService.findUserInfo(user.getId());

        //then
        assertEquals("donghwan", resultUserDto.getNickname());
    }

    @Test
    @DisplayName("유저가 존재하지 않는 경우우")
   void notFoundUserTest() {
        //given

        //when

        //then
        assertThrows(UserNotFoundException.class, () -> userService.findUserInfo(0L));
    }

}