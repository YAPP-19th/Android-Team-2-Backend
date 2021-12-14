package com.yapp.sharefood.favorite.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.favorite.domain.Favorite;
import com.yapp.sharefood.favorite.exception.FavoriteNotFoundException;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class FavoriteServiceTest {
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

    private Category category;
    private User writerUser;
    private User user;
    private Food food;

    @BeforeEach
    void setUp() {
        category = saveTestCategory("A");
        writerUser = saveTestUser("user1_nick", "user1_name", "oauthId1");
        user = saveTestUser("user2_nick", "user2_name", "oauthId2");
        food = saveTestFood("food title1", writerUser, category, FoodStatus.SHARED);
    }

    @Test
    @DisplayName("최애 추가 성공")
    void favoriteCreate_Success() {
        //given

        //when
        Long favoriteId = favoriteService.createFavorite(user, food.getId());
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

        //when

        //then
        assertThrows(UserNotFoundException.class, () -> favoriteService.createFavorite(User.builder().id(-1L).build(), food.getId()));
    }

    @Test
    @DisplayName("최애 추가 실패 - 존재하지 않는 음식(게시글)")
    void favoriteCreate_Fail_FoodNotFound() {
        //given

        //when

        //then
        assertThrows(FoodNotFoundException.class, () -> favoriteService.createFavorite(user, -1L));
    }

    @Test
    @DisplayName("최애 삭제 성공")
    void favoriteDelete_Success() {
        //given
        Long favoriteId = favoriteService.createFavorite(user, food.getId());

        //when
        favoriteService.deleteFavorite(user, food.getId());
        favoriteRepository.flush();

        //then
        assertThrows(FavoriteNotFoundException.class, () -> favoriteRepository.findById(favoriteId).orElseThrow(FavoriteNotFoundException::new));
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

}