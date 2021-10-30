package com.yapp.sharefood.food.service;

import com.yapp.sharefood.common.exception.NotFoundException;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.dto.request.FoodCreationRequest;
import com.yapp.sharefood.food.repository.FoodRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class FoodServiceTest {

    @Autowired
    FoodService foodService;

    @Autowired
    FoodRepository foodRepository;

    @Test
    @DisplayName("food 내부 값만 저장")
    void saveFood() {
        // given
        FoodCreationRequest request = new FoodCreationRequest();
        request.setTitle("title");
        request.setPrice(1000);
        request.setReviewMsg("reviewMsg");
        request.setFoodStatus(FoodStatus.SHARED);

        // when
        Long saveFoodId = foodService.saveFood(request);
        Food food = foodRepository.findById(saveFoodId)
                .orElseThrow(() -> new NotFoundException(""));

        // then
        assertEquals(food.getId(), saveFoodId);
        assertEquals(FoodStatus.SHARED, food.getFoodStatus());
        assertEquals("title", food.getFoodTitle());
        assertEquals("reviewMsg", food.getReviewMsg());
        assertEquals(1000, food.getPrice());
    }
}