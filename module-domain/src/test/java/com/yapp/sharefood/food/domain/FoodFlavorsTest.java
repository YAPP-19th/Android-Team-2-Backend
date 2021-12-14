package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.common.mockdomain.MockFood;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.domain.FlavorType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    void updateFlavorsTest_Success() {
        // given
        Food mockFood = new MockFood();
        List<FoodFlavor> foodFlavors = new ArrayList<>();
//        foodFlavors.add(new FoodFlavor(mockFood, ));
//        foodFlavors.add(new FoodFlavor(mockFood, ));
//        foodFlavors.add(new FoodFlavor(mockFood, ));
//        foodFlavors.add(new FoodFlavor(mockFood, ));
//        foodFlavors.add(new FoodFlavor(mockFood, ));

        // when
        List<Flavor> changeFlavors = new ArrayList<>();
//        changeFlavors.add()

        // then
    }
}