package com.yapp.sharefood.food.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.dto.request.FoodCreationRequest;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {

    private final FoodRepository foodRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Long saveFood(User user, FoodCreationRequest foodCreationRequest) {
        Category findCategory = categoryRepository.findById(foodCreationRequest.getFoodCategory().getId())
                .orElseThrow(CategoryNotFoundException::new);

        Food food = Food.builder()
                .foodTitle(foodCreationRequest.getTitle())
                .foodStatus(foodCreationRequest.getFoodStatus())
                .price(foodCreationRequest.getPrice())
                .reviewMsg(foodCreationRequest.getReviewMsg())
                .build();
        food.assignWriter(user);
        food.assignCategory(findCategory);

        Food save = foodRepository.save(food);

        return save.getId();
    }
}
