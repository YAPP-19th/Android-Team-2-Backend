package com.yapp.sharefood.favorite.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.favorite.domain.Favorite;
import com.yapp.sharefood.favorite.dto.FavoriteFoodDto;
import com.yapp.sharefood.favorite.dto.request.FavoriteCreationRequest;
import com.yapp.sharefood.favorite.dto.response.FavoriteFoodResponse;
import com.yapp.sharefood.favorite.exception.TooManyFavoriteException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yapp.sharefood.favorite.dto.FavoriteFoodDto.foodToFavoriteFoodDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {
    private static final int MAX_SIZE_OF_FAVORITE_FOOD = 5;

    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final FavoriteRepository favoriteRepository;
    private final CategoryRepository categoryRepository;

    public FavoriteFoodResponse findFavoriteFoods(User user, String categoryName) {
        User findUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        List<Category> categories = findOnePartCategories(categoryName);
        List<FavoriteFoodDto> collect = foodRepository.findFavoriteFoods(findUser, categories).stream()
                .map(food -> foodToFavoriteFoodDto(findUser, food))
                .collect(Collectors.toList());

        return FavoriteFoodResponse.of(collect);
    }

    private List<Category> findOnePartCategories(String categoryName) {
        List<Category> categories = new ArrayList<>();
        Category rootCategory = categoryRepository.findByName(categoryName).orElseThrow(CategoryNotFoundException::new);
        categories.add(rootCategory);

        categories.addAll(rootCategory.getChildCategories().getChildCategories());

        return categories;
    }

    @Transactional
    public Long createFavorite(User user, Long foodId, FavoriteCreationRequest favoriteCreationRequest) {
        Food findFood = foodRepository.findById(foodId).orElseThrow(FoodNotFoundException::new);
        List<Category> categories = findOnePartCategories(favoriteCreationRequest.getCategoryName());

        User findUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        List<Favorite> findUserFavorite = favoriteRepository.findAllByUserAndCategories(user, categories);

        if (findUserFavorite.size() >= MAX_SIZE_OF_FAVORITE_FOOD) {
            throw new TooManyFavoriteException();
        }

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
