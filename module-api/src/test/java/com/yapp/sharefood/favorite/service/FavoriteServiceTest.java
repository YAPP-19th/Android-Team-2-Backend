package com.yapp.sharefood.favorite.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.common.service.IntegrationService;
import com.yapp.sharefood.favorite.domain.Favorite;
import com.yapp.sharefood.favorite.dto.FavoriteFoodDto;
import com.yapp.sharefood.favorite.dto.request.FavoriteCreationRequest;
import com.yapp.sharefood.favorite.dto.response.FavoriteFoodResponse;
import com.yapp.sharefood.favorite.exception.FavoriteNotFoundException;
import com.yapp.sharefood.favorite.exception.TooManyFavoriteException;
import com.yapp.sharefood.favorite.repository.FavoriteRepository;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class FavoriteServiceTest extends IntegrationService {
    @Autowired
    FavoriteService favoriteService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    FoodRepository foodRepository;

    @Autowired
    FavoriteRepository favoriteRepository;

    private Category saveTestCategory(String categoryName) {
        Category category = Category.of(categoryName);
        return categoryRepository.save(category);
    }

    private Category saveTestChildCategory(Category rootCategory, String categoryName) {
        Category category = Category.of(categoryName);
        rootCategory.addChildCategories(category);

        return categoryRepository.save(category);
    }

    private User saveTestUser(String nickname, String name, String oauthId) {
        User user = User.builder()
                .nickname(nickname)
                .name(name)
                .oAuthType(OAuthType.KAKAO)
                .oauthId(oauthId)
                .build();
        return userRepository.save(user);
    }

    private Food saveTestFood(String title, User user, Category category, FoodStatus foodStatus) {
        Food food = Food.builder()
                .foodTitle(title)
                .writer(user)
                .foodStatus(foodStatus)
                .category(category)
                .build();
        return foodRepository.save(food);
    }

    private Favorite saveTestFavorite(User user, Food food) {
        Favorite favorite = Favorite.of(user);
        food.assignFavorite(favorite);
        return favoriteRepository.save(favorite);
    }

    private Favorite createFavorite() {
        Favorite favorite = Favorite.of(favoriteUseUser);
        favorite.assignFood(favoriteFood);
        return favoriteRepository.save(favorite);
    }

    private Category category;
    private User writerUser;
    private User user;
    private User favoriteUseUser;
    private Food favoriteFood;
    private Food food;
    private Favorite favorite;

    @BeforeEach
    void setUp() {
        Category rootCategory = saveTestCategory("음식");
        category = saveTestChildCategory(rootCategory, "A");
        writerUser = saveTestUser("user1_nick", "user1_name", "oauthId1");
        user = saveTestUser("user2_nick", "user2_name", "oauthId2");
        food = saveTestFood("food title1", writerUser, category, FoodStatus.SHARED);

        favoriteUseUser = saveTestUser("favorite user", "mock_name", "oauthId");
        favoriteFood = saveTestFood("favorite food title1", writerUser, category, FoodStatus.SHARED);
        favorite = createFavorite();
    }

    @Test
    @DisplayName("최애 추가 성공")
    void favoriteCreate_Success() {
        //given
        FavoriteCreationRequest favoriteCreationRequest = FavoriteCreationRequest.of("음식");

        //when
        Long favoriteId = favoriteService.createFavorite(user, food.getId(), favoriteCreationRequest);
        Favorite findFavorite = favoriteRepository.findById(favoriteId).orElseThrow(FavoriteNotFoundException::new);

        //then
        assertEquals(favoriteId, findFavorite.getId());
        assertEquals(user, findFavorite.getUser());
        assertEquals(food, findFavorite.getFood());
    }

    @Test
    @DisplayName("최애 추가 실패 - 존재하지 않는 사용자")
    void favoriteCreate_Fail_UserNotFound() {
        //given
        FavoriteCreationRequest favoriteCreationRequest = FavoriteCreationRequest.of("음식");

        //when

        //then
        assertThrows(UserNotFoundException.class, () -> favoriteService.createFavorite(User.builder().id(-1L).build(), food.getId(), favoriteCreationRequest));
    }

    @Test
    @DisplayName("최애 추가 실패 - 존재하지 않는 음식(게시글)")
    void favoriteCreate_Fail_FoodNotFound() {
        //given
        FavoriteCreationRequest favoriteCreationRequest = FavoriteCreationRequest.of("음식");

        //when

        //then
        assertThrows(FoodNotFoundException.class, () -> favoriteService.createFavorite(user, -1L, favoriteCreationRequest));
    }

    @Test
    @DisplayName("최애 추가 실패 - 최애는 5개를 넘을 수 없습니다")
    void createFavoriteTest_Fail_TooManyFavorite() {
        //given
        Food food1 = saveTestFood("test1", user, category, FoodStatus.SHARED);
        Food food2 = saveTestFood("test2", user, category, FoodStatus.SHARED);
        Food food3 = saveTestFood("test3", user, category, FoodStatus.SHARED);
        Food food4 = saveTestFood("test4", user, category, FoodStatus.SHARED);
        Food food5 = saveTestFood("test5", user, category, FoodStatus.SHARED);
        List<Food> foodList = List.of(food1, food2, food3, food4, food5);

        for (Food food : foodList) {
            saveTestFavorite(user, food);
        }
        FavoriteCreationRequest favoriteCreationRequest = FavoriteCreationRequest.of("음식");

        //when

        //then
        Food testFood = saveTestFood("testFood", user, category, FoodStatus.SHARED);
        assertThrows(TooManyFavoriteException.class, () -> favoriteService.createFavorite(user, testFood.getId(), favoriteCreationRequest));
    }

    @Test
    @DisplayName("최애 삭제 성공")
    void favoriteDelete_Success() {
        //given
        //when
        favoriteService.deleteFavorite(favoriteUseUser, favoriteFood.getId());
        Food food = foodRepository.findById(favoriteFood.getId()).orElseThrow(FoodNotFoundException::new);

        //then
        assertThat(food.getFavorites().getFavorites()).isEmpty();
    }

    @Test
    @DisplayName("최애 삭제 실패 - 존재하지 않는 사용자")
    void favoriteDelete_Fail_UserNotFound() {
        //given

        //when

        //then
        assertThrows(UserNotFoundException.class, () -> favoriteService.deleteFavorite(User.builder().id(-1L).build(), food.getId()));
    }

    @Test
    @DisplayName("최애 삭제 실패 - 존재하지 않는 음식(게시글)")
    void favoriteDelete_Fail_FoodNotFound() {
        //given

        //when

        //then
        assertThrows(FoodNotFoundException.class, () -> favoriteService.deleteFavorite(user, -1L));
    }

    @Test
    @DisplayName("최애 조회 성공")
    void favoriteFind_Success() {
        //given
        Food food1 = saveTestFood("test1", writerUser, category, FoodStatus.MINE);
        Food food2 = saveTestFood("test2", user, category, FoodStatus.SHARED);
        Food food3 = saveTestFood("test3", writerUser, category, FoodStatus.SHARED);
        Food food4 = saveTestFood("test4", user, category, FoodStatus.MINE);
        List<Food> foodList = List.of(food1, food2, food3, food4);

        for (Food food : foodList) {
            saveTestFavorite(user, food);
        }

        //when
        FavoriteFoodResponse favoriteFoods = favoriteService.findFavoriteFoods(user, category.getName());

        //then
        int expectFavoriteFoodSize = foodList.size();
        int actualFavoriteFoodSize = favoriteFoods.getFavoriteFoods().size();
        assertEquals(expectFavoriteFoodSize, actualFavoriteFoodSize);
        for (int i = 0; i < foodList.size(); i++) {
            FavoriteFoodDto expectFood = FavoriteFoodDto.foodToFavoriteFoodDto(user, foodList.get(i));
            FavoriteFoodDto actualFood = favoriteFoods.getFavoriteFoods().get(i);
            assertEquals(expectFood, actualFood);
        }
    }

    @Test
    @DisplayName("최애 조회 실패 - 존재하지 않는 유저")
    void favoriteFood_Fail_UserNotFound() {
        //given
        String categoryName = category.getName();
        User mockUser = User.builder().id(-1L).build();

        //when

        //then
        assertThrows(UserNotFoundException.class, () -> favoriteService.findFavoriteFoods(mockUser, categoryName));
    }

}