package com.yapp.sharefood.food.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodReportStatus;
import com.yapp.sharefood.food.domain.FoodReportType;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class FoodReportServiceTest {
    @Autowired
    FoodReportService foodReportService;

    @Autowired
    FoodRepository foodRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;

    private User saveTestUser(String nickname, String name, String oauthId) {
        User user = User.builder()
                .nickname(nickname)
                .name(name)
                .oAuthType(OAuthType.KAKAO)
                .oauthId(oauthId)
                .build();
        return userRepository.save(user);
    }

    private Category saveTestCategory(String categoryName) {
        Category category = Category.of(categoryName);
        return categoryRepository.save(category);
    }

    private Category saveTestCategoryWithParent(String categoryName, Category parent) {
        Category category = Category.of(categoryName);
        category.assignParent(parent);
        return categoryRepository.save(category);
    }

    private Food saveFood(String title, User user, Category category) {
        Food food = Food.builder()
                .foodTitle(title)
                .writer(user)
                .foodStatus(FoodStatus.SHARED)
                .category(category)
                .build();

        return foodRepository.save(food);
    }

    @Test
    void createReport_Success_Posting_Duplicated() {
        //given
        User savedUser = saveTestUser("nickname", "name", "oauthId");
        Category savedCategory = saveTestCategory("donghwan");
        Food savedFood = saveFood("title", savedUser, savedCategory);

        //when
        foodReportService.createReport(savedFood.getId(), FoodReportType.POSTING_DUPLICATED_CONTENT.getMessage());

        //then
        FoodReportStatus status = savedFood.getReportStatus();
        int point = savedFood.getReportPoint();
        assertEquals(1, point);
        assertEquals(FoodReportStatus.NORMAL, status);
    }

    @Test
    void createReport_Success_Posting_Advertising() {
        //given
        User savedUser = saveTestUser("nickname", "name", "oauthId");
        Category savedCategory = saveTestCategory("donghwan");
        Food savedFood = saveFood("title", savedUser, savedCategory);

        //when
        foodReportService.createReport(savedFood.getId(), FoodReportType.POSTING_ADVERTISING_CONTENT.getMessage());

        //then
        FoodReportStatus status = savedFood.getReportStatus();
        int point = savedFood.getReportPoint();
        assertEquals(1, point);
        assertEquals(FoodReportStatus.NORMAL, status);
    }

    @Test
    void createReport_Success_Posting_No_Relation() {
        //given
        User savedUser = saveTestUser("nickname", "name", "oauthId");
        Category savedCategory = saveTestCategory("donghwan");
        Food savedFood = saveFood("title", savedUser, savedCategory);

        //when
        foodReportService.createReport(savedFood.getId(), FoodReportType.POSTING_NO_RELATION_CONTENT.getMessage());

        //then
        FoodReportStatus status = savedFood.getReportStatus();
        int point = savedFood.getReportPoint();
        assertEquals(1, point);
        assertEquals(FoodReportStatus.NORMAL, status);
    }

    @Test
    void createReport_Success_Posting_Wrong() {
        //given
        User savedUser = saveTestUser("nickname", "name", "oauthId");
        Category savedCategory = saveTestCategory("donghwan");
        Food savedFood = saveFood("title", savedUser, savedCategory);

        //when
        foodReportService.createReport(savedFood.getId(), FoodReportType.POSTING_WRONG_CONTENT.getMessage());

        //then
        FoodReportStatus status = savedFood.getReportStatus();
        int point = savedFood.getReportPoint();
        assertEquals(1, point);
        assertEquals(FoodReportStatus.NORMAL, status);
    }

    @Test
    void createReport_Success_Posting_Etc() {
        //given
        User savedUser = saveTestUser("nickname", "name", "oauthId");
        Category savedCategory = saveTestCategory("donghwan");
        Food savedFood = saveFood("title", savedUser, savedCategory);

        //when
        foodReportService.createReport(savedFood.getId(), FoodReportType.POSTING_ETC_CONTENT.getMessage());

        //then
        FoodReportStatus status = savedFood.getReportStatus();
        int point = savedFood.getReportPoint();
        assertEquals(1, point);
        assertEquals(FoodReportStatus.NORMAL, status);
    }

    @Test
    void createReport_Success_Posting_Sadistic_And_Harmful() {
        //given
        User savedUser = saveTestUser("nickname", "name", "oauthId");
        Category savedCategory = saveTestCategory("donghwan");
        Food savedFood = saveFood("title", savedUser, savedCategory);

        //when
        foodReportService.createReport(savedFood.getId(), FoodReportType.POSTING_SADISTIC_AND_HARMFUL_CONTENT.getMessage());

        //then
        FoodReportStatus status = savedFood.getReportStatus();
        int point = savedFood.getReportPoint();
        assertEquals(99, point);
        assertEquals(FoodReportStatus.BANNDED, status);
    }

    @Test
    void createReport_Success_Posting_Obscene() {
        //given
        User savedUser = saveTestUser("nickname", "name", "oauthId");
        Category savedCategory = saveTestCategory("donghwan");
        Food savedFood = saveFood("title", savedUser, savedCategory);

        //when
        foodReportService.createReport(savedFood.getId(), FoodReportType.POSTING_OBSCENE_CONTENT.getMessage());

        //then
        FoodReportStatus status = savedFood.getReportStatus();
        int point = savedFood.getReportPoint();
        assertEquals(99, point);
        assertEquals(FoodReportStatus.BANNDED, status);
    }

    @Test
    void createReport_Success_Food_Judge() {
        //given
        User savedUser = saveTestUser("nickname", "name", "oauthId");
        Category savedCategory = saveTestCategory("donghwan");
        Food savedFood = saveFood("title", savedUser, savedCategory);

        //when
        foodReportService.createReport(savedFood.getId(), FoodReportType.POSTING_ETC_CONTENT.getMessage());
        foodReportService.createReport(savedFood.getId(), FoodReportType.POSTING_ETC_CONTENT.getMessage());
        foodReportService.createReport(savedFood.getId(), FoodReportType.POSTING_ETC_CONTENT.getMessage());

        //then
        FoodReportStatus status = savedFood.getReportStatus();
        int point = savedFood.getReportPoint();
        assertEquals(3, point);
        assertEquals(FoodReportStatus.JUDGED, status);
    }

    @Test
    void createReport_Fail_Food_Not_Found() {
        //given

        //when

        //then
        assertThrows(FoodNotFoundException.class, () -> foodReportService.createReport(1L, FoodReportType.POSTING_OBSCENE_CONTENT.getMessage()));
    }

    @Test
    void createReport_Fail_User_Not_Found() {
        //given
        User savedUser = saveTestUser("nickname","name","oauthId");
        Category savedCategory = saveTestCategory("donghwan");
        Food savedFood = saveFood("title", savedUser, savedCategory);
        userRepository.delete(savedUser);

        //when

        //then
        Long savedFoodId = savedFood.getId();
        assertThrows(UserNotFoundException.class, () -> foodReportService.createReport(savedFoodId, FoodReportType.POSTING_OBSCENE_CONTENT.getMessage()));
    }
}