package com.yapp.sharefood.user.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodReportType;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.domain.UserReportStatus;
import com.yapp.sharefood.user.domain.UserReportType;
import com.yapp.sharefood.user.dto.request.UserNicknameRequest;
import com.yapp.sharefood.user.exception.UserBanndedException;
import com.yapp.sharefood.user.exception.UserNicknameExistException;
import com.yapp.sharefood.user.rand.UserNicknameRandomComponent;
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
    UserNicknameRandomComponent userNicknameRandomComponent;


    @Test
    @DisplayName("유니크한 nickname 생성")
    void uniqueNicknameCreatTest() {
        // given
        willReturn("randomName")
                .given(userNicknameRandomComponent).createRandomUserNickname();

        // when
        String uniqueNickname = userService.createUniqueNickname().getNickname();

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
                .given(userNicknameRandomComponent).createRandomUserNickname();

        // when

        // then
        assertThrows(UserNicknameExistException.class, () -> userService.createUniqueNickname());
    }

    @Test
    @DisplayName("중복되는 닉네임이 없는 경우")
    void nicknameNotDuplicateTest() {
        //given
        String nickname = "1";

        //when

        //then
        userService.checkNicknameDuplicate(nickname);
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

        //when

        //then
        assertThrows(UserNicknameExistException.class, () -> userService.checkNicknameDuplicate(nickname));
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
        String resultNickname = userService.changeUserNickname(user.getId(), request).getNickname();

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
    @DisplayName("내 유저정보 조회 성공")
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
        String userNickname = userService.findUserInfo(user.getId()).getUserInfo().getNickname();

        //then
        assertEquals("donghwan", userNickname);
    }

    @Test
    @DisplayName("내 유저가 존재하지 않는 경우")
    void notFoundUserTest() {
        //given

        //when

        //then
        assertThrows(UserNotFoundException.class, () -> userService.findUserInfo(0L));
    }

    @Test
    @DisplayName("다른 유저정보 조회 성공")
    void findOtherUserTest() {
        //given
        User user = User.builder()
                .name("donghwan")
                .nickname("donghwan")
                .oauthId("kakao-id")
                .oAuthType(OAuthType.KAKAO)
                .build();

        userRepository.save(user);

        //when
        String userNickname = userService.findOtherUserInfo(user.getId()).getUserInfo().getNickname();

        //then
        assertEquals("donghwan", userNickname);
    }

    @Test
    @DisplayName("다른 유저정보 조회 실패 - 해당 다른 유저가 존재하지 않는 경우")
    void notFoundOtherUserTest() {
        //given

        //when

        //then
        assertThrows(UserNotFoundException.class, () -> userService.findOtherUserInfo(0L));
    }

    @Test
    @DisplayName("다른 유저정보 조회 실패 - 해당 다른 유저가 존재하지 않는 경우")
    void findOtherUserInfo_Fail_User_Bannded() {
        //given
        User user = User.builder()
                .name("donghwan")
                .nickname("donghwan")
                .oauthId("kakao-id")
                .oAuthType(OAuthType.KAKAO)
                .build();

        user.addReport(UserReportType.POSTING_OBSCENE_USER.getMessage());

        userRepository.save(user);

        //when

        //then
        Long userId = user.getId();
        assertThrows(UserBanndedException.class, () -> userService.findOtherUserInfo(userId));
    }

    @Test
    @DisplayName("신고 성공 - 반복적 중복된 게시글")
    void userReport_Success_Posting_Duplicate() {
        //given
        User user = User.builder()
                .name("donghwan")
                .nickname("donghwan")
                .oauthId("kakao-id")
                .oAuthType(OAuthType.KAKAO)
                .build();

        userRepository.save(user);

        //when
        user.addReport(UserReportType.POSTING_DUPLICATED_USER.getMessage());

        //then
        int point = user.getReportPoint();

        UserReportStatus reportStatus = user.getReportStatus();

        assertEquals(1, point);
        assertEquals(UserReportStatus.NORMAL, reportStatus);
    }

    @Test
    @DisplayName("신고 성공 - 반복적 광고성 게시글")
    void userReport_Success_Posting_Advertising() {
        //given
        User user = User.builder()
                .name("donghwan")
                .nickname("donghwan")
                .oauthId("kakao-id")
                .oAuthType(OAuthType.KAKAO)
                .build();

        userRepository.save(user);

        //when
        user.addReport(UserReportType.POSTING_ADVERTISING_USER.getMessage());

        //then
        int point = user.getReportPoint();

        UserReportStatus reportStatus = user.getReportStatus();

        assertEquals(1, point);
        assertEquals(UserReportStatus.NORMAL, reportStatus);
    }

    @Test
    @DisplayName("신고 성공 - 반복적 관계없는 게시글")
    void userReport_Success_Posting_No_Relation() {
        //given
        User user = User.builder()
                .name("donghwan")
                .nickname("donghwan")
                .oauthId("kakao-id")
                .oAuthType(OAuthType.KAKAO)
                .build();

        userRepository.save(user);

        //when
        user.addReport(UserReportType.POSTING_NO_RELATION_USER.getMessage());

        //then
        int point = user.getReportPoint();

        UserReportStatus reportStatus = user.getReportStatus();

        assertEquals(1, point);
        assertEquals(UserReportStatus.NORMAL, reportStatus);
    }

    @Test
    @DisplayName("신고 성공 - 반복적 잘못된 정보 게시글")
    void userReport_Success_Posting_Wrong() {
        //given
        User user = User.builder()
                .name("donghwan")
                .nickname("donghwan")
                .oauthId("kakao-id")
                .oAuthType(OAuthType.KAKAO)
                .build();

        userRepository.save(user);

        //when
        user.addReport(UserReportType.POSTING_WRONG_USER.getMessage());

        //then
        int point = user.getReportPoint();

        UserReportStatus reportStatus = user.getReportStatus();

        assertEquals(1, point);
        assertEquals(UserReportStatus.NORMAL, reportStatus);
    }

    @Test
    @DisplayName("신고 성공 - 기타")
    void userReport_Success_Posting_Etc() {
        //given
        User user = User.builder()
                .name("donghwan")
                .nickname("donghwan")
                .oauthId("kakao-id")
                .oAuthType(OAuthType.KAKAO)
                .build();

        userRepository.save(user);

        //when
        user.addReport(UserReportType.POSTING_ETC_USER.getMessage());

        //then
        int point = user.getReportPoint();

        UserReportStatus reportStatus = user.getReportStatus();

        assertEquals(1, point);
        assertEquals(UserReportStatus.NORMAL, reportStatus);
    }

    @Test
    @DisplayName("신고 성공 - 반복적으로 가학적 유해한 게시글")
    void userReport_Success_Posting_Sadistic_And_Harmful() {
        //given
        User user = User.builder()
                .name("donghwan")
                .nickname("donghwan")
                .oauthId("kakao-id")
                .oAuthType(OAuthType.KAKAO)
                .build();

        userRepository.save(user);

        //when
        user.addReport(UserReportType.POSTING_SADISTIC_AND_HARMFUL_USER.getMessage());

        //then
        int point = user.getReportPoint();

        UserReportStatus reportStatus = user.getReportStatus();

        assertEquals(99, point);
        assertEquals(UserReportStatus.BANNDED, reportStatus);
    }

    @Test
    @DisplayName("신고 성공 - 반복적으로 음란물")
    void userReport_Success_Posting_Obscene() {
        //given
        User user = User.builder()
                .name("donghwan")
                .nickname("donghwan")
                .oauthId("kakao-id")
                .oAuthType(OAuthType.KAKAO)
                .build();

        userRepository.save(user);

        //when
        user.addReport(UserReportType.POSTING_OBSCENE_USER.getMessage());

        //then
        int point = user.getReportPoint();

        UserReportStatus reportStatus = user.getReportStatus();

        assertEquals(99, point);
        assertEquals(UserReportStatus.BANNDED, reportStatus);
    }

    @Test
    @DisplayName("신고 성공 - 신고 누적으로 인한 정지")
    void userReport_Success_Point_Over_5() {
        //given
        User user = User.builder()
                .name("donghwan")
                .nickname("donghwan")
                .oauthId("kakao-id")
                .oAuthType(OAuthType.KAKAO)
                .build();

        userRepository.save(user);

        //when
        user.addReport(UserReportType.POSTING_ETC_USER.getMessage());
        user.addReport(UserReportType.POSTING_ETC_USER.getMessage());
        user.addReport(UserReportType.POSTING_ETC_USER.getMessage());
        user.addReport(UserReportType.POSTING_ETC_USER.getMessage());
        user.addReport(UserReportType.POSTING_ETC_USER.getMessage());

        //then
        int point = user.getReportPoint();

        UserReportStatus reportStatus = user.getReportStatus();

        assertEquals(5, point);
        assertEquals(UserReportStatus.BANNDED, reportStatus);
    }
}