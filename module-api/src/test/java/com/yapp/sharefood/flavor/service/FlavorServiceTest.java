package com.yapp.sharefood.flavor.service;

import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.dto.response.FlavorsResponse;
import com.yapp.sharefood.flavor.repository.FlavorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FlavorServiceTest {

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
        assertEquals(2, allFlavors.getFavors().size());
        assertThat(allFlavors.getFavors())
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
        assertEquals(0, allFlavorsResponse.getFavors().size());
    }
}