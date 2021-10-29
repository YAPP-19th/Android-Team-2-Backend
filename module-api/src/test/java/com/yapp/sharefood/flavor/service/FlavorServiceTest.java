package com.yapp.sharefood.flavor.service;

import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.dto.FlavorDto;
import com.yapp.sharefood.flavor.dto.request.UserFlavorRequest;
import com.yapp.sharefood.flavor.dto.response.FlavorsResponse;
import com.yapp.sharefood.flavor.exception.FlavorNotFoundException;
import com.yapp.sharefood.flavor.repository.FlavorRepository;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class FlavorServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    FlavorService flavorService;
    @Autowired
    FlavorRepository flavorRepository;

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
        UserFlavorRequest request = new UserFlavorRequest();
        request.setFlavors(findFlavors.stream().map(flavor -> FlavorDto.of(flavor.getId(), flavor.getFlavorType())).collect(Collectors.toList()));

        //when
        int result = flavorService.updateUserFlavors(user, request).getUpdateSuccessCount();

        //then
        assertEquals(2, result);
    }

    @Test
    @DisplayName("요청이 들어온 Flavor를 찾을 수 없는 경우")
    void failUpdateUserFlavorCauseNotFoundFlavorTest() {
        User user = User.builder().name("donghwan")
                .nickname("donghwan")
                .oAuthType(OAuthType.KAKAO)
                .oauthId("oauth-id")
                .build();
        userRepository.save(user);

        FlavorDto flavorDto = FlavorDto.of(-1L, FlavorType.SPICY);
        ArrayList<FlavorDto> flavorDtos = new ArrayList();
        flavorDtos.add(flavorDto);

        List<Flavor> findFlavors = flavorRepository.findAll();
        UserFlavorRequest request = new UserFlavorRequest();
        request.setFlavors(flavorDtos);

        //when

        //then
        assertThrows(FlavorNotFoundException.class, () -> flavorService.updateUserFlavors(user, request));
    }
}