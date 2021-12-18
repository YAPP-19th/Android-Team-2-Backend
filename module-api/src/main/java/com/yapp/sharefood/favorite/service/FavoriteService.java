package com.yapp.sharefood.favorite.service;

import com.yapp.sharefood.favorite.domain.Favorite;
import com.yapp.sharefood.favorite.repository.FavoriteRepository;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FavoriteService {
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public Long createFavorite(User user, Long foodId) {
        User findUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);

        Food findFood = foodRepository.findById(foodId).orElseThrow(FoodNotFoundException::new);

        Favorite favorite = Favorite.of(findUser);
        findFood.assignFavorite(favorite);
        favoriteRepository.flush();

        return favorite.getId();
    }

    @Transactional
    public void deleteFavorite(User user, Long foodId) {
        User findUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);

        Food findFood = foodRepository.findById(foodId).orElseThrow(FoodNotFoundException::new);
        findFood.deleteFavorite(findUser);
    }
}