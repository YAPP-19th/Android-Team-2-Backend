package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.common.mockdomain.MockFood;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.domain.FlavorType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class FoodFlavorsTest {
    static Map<Long, Flavor> flavorDb = new HashMap<>();

    @BeforeAll
    static void setUpAll() {
        flavorDb.put(1L, Flavor.of(FlavorType.SPICY));
        flavorDb.put(2L, Flavor.of(FlavorType.PLAIN_DETAIL));
        flavorDb.put(3L, Flavor.of(FlavorType.SALTY));
        flavorDb.put(4L, Flavor.of(FlavorType.BITTER));
        flavorDb.put(5L, Flavor.of(FlavorType.SOUR));
    }


    @Test
    @DisplayName("flavor update 기능 테스트 - 성공")
    void updateFlavorsTest_Success() {
        // given
        Food mockFood = new MockFood();
        mockFood.assignFlavors(List.of(flavorDb.get(1L), flavorDb.get(2L), flavorDb.get(3L)));

        List<Flavor> changeFlavors = new ArrayList<>();
        changeFlavors.add(flavorDb.get(3L));
        changeFlavors.add(flavorDb.get(4L));
        changeFlavors.add(flavorDb.get(5L));

        // when
        mockFood.getFoodFlavors()
                .updateFlavors(changeFlavors, mockFood);

        // then
        assertThat(mockFood.getFoodFlavors().getFoodFlavors())
                .isNotNull()
                .hasSize(3);

        List<FlavorType> assignedFlavorTypes = mockFood.getFoodFlavors().getFoodFlavors().stream()
                .map(foodFlavor -> foodFlavor.getFlavor().getFlavorType())
                .collect(Collectors.toList());
        assertThat(assignedFlavorTypes)
                .isNotNull()
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(List.of(FlavorType.SALTY, FlavorType.BITTER, FlavorType.SOUR));
    }

    @Test
    @DisplayName("flavor update 빈 값인 경우 - 성공")
    void updateFlavorsEmptyListTest_Success() {
        // given
        Food mockFood = new MockFood();
        mockFood.assignFlavors(List.of(flavorDb.get(1L), flavorDb.get(2L), flavorDb.get(3L)));

        List<Flavor> changeFlavors = new ArrayList<>();

        // when
        mockFood.getFoodFlavors()
                .updateFlavors(changeFlavors, mockFood);

        // then
        assertThat(mockFood.getFoodFlavors().getFoodFlavors())
                .isNotNull()
                .hasSize(0);
    }
}