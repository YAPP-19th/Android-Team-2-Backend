package com.yapp.sharefood.like.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.common.exception.ForbiddenException;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.common.service.IntegrationService;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.like.domain.Like;
import com.yapp.sharefood.like.repository.LikeRepository;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class LikeServiceTest extends IntegrationService {
    @Autowired
    LikeService likeService;

    @Autowired
    FoodRepository foodRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    LikeRepository likeRepository;

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

    private Food saveFood(String title, User user, Category category, FoodStatus foodStatus) {
        Food food = Food.builder()
                .foodTitle(title)
                .writer(user)
                .foodStatus(foodStatus)
                .category(category)
                .build();

        return foodRepository.save(food);
    }

    @Test
    void saveLikeTest_Success() {
        Category saveCategory = saveTestCategory("A");
        User user1 = saveTestUser("user1_nick", "user1_name", "oauthId1");
        User user2 = saveTestUser("user2_nick", "user2_name", "oauthId2");

        Food food = saveFood("food title1", user1, saveCategory, FoodStatus.SHARED);

        // when
        Long likeId = likeService.saveLike(user2, food.getId());
        Like like = likeRepository.findById(likeId)
                .orElseThrow();

        // then
        assertEquals(food.getId(), like.getFood().getId());
        assertEquals(user2.getId(), like.getUser().getId());
    }

    @Test
    @DisplayName("????????? like ?????? ????????????")
    void saveLikeMyTest_Success() throws Exception {
        Category saveCategory = saveTestCategory("A");
        User user1 = saveTestUser("user1_nick", "user1_name", "oauthId1");

        Food food = saveFood("food title", user1, saveCategory, FoodStatus.SHARED);

        // when
        Long likeId = likeService.saveLike(user1, food.getId());
        Like like = likeRepository.findById(likeId)
                .orElseThrow();

        // then
        assertEquals(food.getId(), like.getFood().getId());
        assertEquals(user1.getId(), like.getUser().getId());
    }

    @Test
    @DisplayName("food??? private??? ??? like ?????? ?????? ?????????")
    void saveLikeExceptionWhenFoodIsPrivateTest_InvalidError() throws Exception {
        Category saveCategory = saveTestCategory("A");
        User user1 = saveTestUser("user1_nick", "user1_name", "oauthId1");
        Food food = saveFood("food title", user1, saveCategory, FoodStatus.MINE);

        // when

        // then
        long foodId = food.getId();
        assertThrows(InvalidOperationException.class, () -> likeService.saveLike(user1, foodId));
    }

    @Test
    @DisplayName("?????? food??? like ?????? ?????? ??????")
    void saveFoodNotFoundExceptionTest_NotFoundException() throws Exception {
        // given
        User user1 = saveTestUser("user1_nick", "user1_name", "oauthId1");

        // when

        // then
        assertThrows(FoodNotFoundException.class, () -> likeService.saveLike(user1, 1L));
    }

    @Test
    @DisplayName("like ?????? ?????? ?????????")
    void deleteLikeTest_Success() {
        // given
        Category saveCategory = saveTestCategory("A");
        User user1 = saveTestUser("user1_nick", "user1_name", "oauthId1");
        Food food = saveFood("food title", user1, saveCategory, FoodStatus.SHARED);
        Like like = Like.of(user1);
        food.assignLike(like);
        likeRepository.flush();

        // when
        likeService.deleteLike(user1, food.getId());
        Food findFood = foodRepository.findById(food.getId())
                .orElseThrow();

        // then
        assertEquals(0, findFood.getLikes().getSize());
    }

    @Test
    @DisplayName("like ????????? ????????? ?????? food??? like ?????? ????????? ?????? ??????")
    void deleteLikeFailTest_ForbiddenException() throws Exception {
        // given
        Category saveCategory = saveTestCategory("A");
        User user1 = saveTestUser("user1_nick", "user1_name", "oauthId1");
        Food food = saveFood("food title", user1, saveCategory, FoodStatus.SHARED);
        Like like = Like.of(user1);
        food.assignLike(like);
        likeRepository.flush();

        User user2 = saveTestUser("user2_nick", "user2_name", "oauthId2");

        // when

        // then
        long foodId = food.getId();
        assertThrows(ForbiddenException.class, () -> likeService.deleteLike(user2, foodId));
    }

    @Test
    @DisplayName("?????? food??? like ?????? ?????? ??????")
    void deleteFoodNotFoundExceptionTest_NotFoundException() throws Exception {
        // given
        User user1 = saveTestUser("user1_nick", "user1_name", "oauthId1");

        // when

        // then
        assertThrows(FoodNotFoundException.class, () -> likeService.deleteLike(user1, 1L));
    }
}