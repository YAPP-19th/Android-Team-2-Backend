package com.yapp.sharefood.flavor.service;

import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.dto.request.UserFlavorRequest;
import com.yapp.sharefood.flavor.dto.response.FlavorsResponse;
import com.yapp.sharefood.flavor.dto.response.UpdateUserFlavorResponse;
import com.yapp.sharefood.flavor.exception.FlavorNotFoundException;
import com.yapp.sharefood.flavor.repository.FlavorRepository;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import com.yapp.sharefood.userflavor.domain.UserFlavor;
import com.yapp.sharefood.userflavor.repository.UserFlavorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class FlavorServiceTest {

    @Autowired
    FlavorService flavorService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FlavorRepository flavorRepository;
    @Autowired
    UserFlavorRepository userFlavorRepository;

    @Test
    @DisplayName("하나 이상 flavor가 존재하는 겨우")
    void findAllFlavorsTest() {
        // given
        flavorRepository.save(Flavor.of(FlavorType.SWEET));
        flavorRepository.save(Flavor.of(FlavorType.SPICY));

        // when
        FlavorsResponse allFlavors = flavorService.findAllFlavors();

        // then
        assertEquals(2, allFlavors.getFlavors().size());
        assertThat(allFlavors.getFlavors())
                .extracting("flavorName")
                .contains(FlavorType.SWEET.getFlavorName(), FlavorType.SPICY.getFlavorName());
    }

    @Test
    @DisplayName("flavor가 아무 것도 없는 경우")
    void findAllZeroIfEmptyTest() throws Exception {
        // given

        // when
        FlavorsResponse allFlavorsResponse = flavorService.findAllFlavors();

        // then
        assertEquals(0, allFlavorsResponse.getFlavors().size());
    }

    @Test
    @DisplayName("Flavor 최신화 성공")
    void updateUserFlavorTest() {
        //given
        User user = User.builder().name("donghwan")
                .nickname("donghwan")
                .oAuthType(OAuthType.KAKAO)
                .oauthId("oauth-id")
                .build();
        userRepository.save(user);

        Flavor flavor1 = Flavor.of(FlavorType.SPICY);
        flavorRepository.save(flavor1);
        Flavor flavor2 = Flavor.of(FlavorType.SWEET);
        flavorRepository.save(flavor2);

        List<Flavor> findFlavors = flavorRepository.findAll();
        UserFlavorRequest request = UserFlavorRequest.of(findFlavors.stream()
                .map(flavor -> flavor.getFlavorType().getFlavorName())
                .collect(Collectors.toList()));

        //when
        FlavorsResponse response = flavorService.updateUserFlavors(user, request);

        //then
        assertThat(response.getFlavors())
                .isNotNull()
                .hasSize(2)
                .extracting("flavorName")
                .containsExactlyInAnyOrderElementsOf(List.of("매운맛", "단맛"));
    }

    @Test
    @DisplayName("요청이 들어온 Flavor를 찾을 수 없는 경우")
    void failUpdateUserFlavorCauseNotFoundFlavorTest() {
        //given
        User user = User.builder().name("donghwan")
                .nickname("donghwan")
                .oAuthType(OAuthType.KAKAO)
                .oauthId("oauth-id")
                .build();
        userRepository.save(user);

        UserFlavorRequest request = UserFlavorRequest.of(List.of("존재 하지 않는 맛"));

        //when

        //then
        assertThrows(FlavorNotFoundException.class, () -> flavorService.updateUserFlavors(user, request));
    }

    @Test
    @DisplayName("유저 정보 기반 선호하는 Flavor 불러오는 상황")
    void findUserFlavorByUserId() {
        //given
        User user = User.builder().name("donghwan")
                .nickname("donghwan")
                .oAuthType(OAuthType.KAKAO)
                .oauthId("oauth-id")
                .build();
        userRepository.save(user);

        Flavor flavor1 = Flavor.of(FlavorType.SPICY);
        flavorRepository.save(flavor1);
        Flavor flavor2 = Flavor.of(FlavorType.SWEET);
        flavorRepository.save(flavor2);

        UserFlavor userFlavor1 = UserFlavor.of(user, flavor1);
        userFlavorRepository.save(userFlavor1);
        UserFlavor userFlavor2 = UserFlavor.of(user, flavor2);
        userFlavorRepository.save(userFlavor2);

        //when
        FlavorsResponse response = flavorService.findUserFlavors(user);

        //then
        assertEquals(2, response.getFlavors().size());
        assertThat(response.getFlavors())
                .extracting("flavorName")
                .contains(flavor1.getFlavorType().getFlavorName(), flavor2.getFlavorType().getFlavorName());
    }

    @Test
    @DisplayName("")
    void notFoundUserFlavorByUserIdCauseNotFoundUser() {
        //given

        //when

        //then
        assertThrows(UserNotFoundException.class, () -> flavorService.findUserFlavors(User.builder().id(-1L).build()));
    }
}