package com.yapp.sharefood.like.service;

import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.like.domain.Like;
import com.yapp.sharefood.like.repository.LikeRepository;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final FoodRepository foodRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public Long saveLike(User user, Long foodId, String categoryName) {
        Food findFood = findCategoryIncludeFood(foodId, categoryName);

        Like like = Like.of(user);
        findFood.assignLike(like);
        likeRepository.flush();

        return like.getId();
    }

    @Transactional
    public void deleteLike(User user, Long foodId, String categoryName) {
        Food findFood = findCategoryIncludeFood(foodId, categoryName);
        findFood.deleteLike(user);
    }

    private Food findCategoryIncludeFood(Long foodId, String categoryName) {
        Food findFood = foodRepository.findByIdWithCategory(foodId)
                .orElseThrow(FoodNotFoundException::new);
        validateFoodCategory(findFood, categoryName);

        return findFood;
    }

    private void validateFoodCategory(Food food, String categoryName) {
        if (!food.getCategory().getName().equals(categoryName)) {
            throw new FoodNotFoundException();
        }
    }
}
