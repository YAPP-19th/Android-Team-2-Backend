package com.yapp.sharefood.food.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.common.exception.NotFoundException;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodIngredientType;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.domain.TagWrapper;
import com.yapp.sharefood.food.dto.FoodCategoryDto;
import com.yapp.sharefood.food.dto.request.FoodCreationRequest;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.tag.domain.Tag;
import com.yapp.sharefood.tag.repository.TagRepository;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class FoodServiceTest {

    @Autowired
    FoodService foodService;

    @Autowired
    FoodRepository foodRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    TagRepository tagRepository;

    private Category saveTestCategory(String categoryName) {
        Category category = Category.of(categoryName);
        return categoryRepository.save(category);
    }

    private User saveTestUser(String nickname, String name) {
        User user = User.builder()
                .nickname(nickname)
                .name(name)
                .oAuthType(OAuthType.KAKAO)
                .oauthId("oauthId")
                .build();
        return userRepository.save(user);
    }

    private Tag saveTag(String tagName) {
        return tagRepository.save(Tag.of(tagName));
    }

    @Test
    @DisplayName("food 내부 값만 저장")
    void saveFood() {
        // given
        User saveUser = saveTestUser("nickname", "name");
        Category saveCategory = saveTestCategory("A");

        FoodCreationRequest request = new FoodCreationRequest();
        request.setTitle("title");
        request.setPrice(1000);
        request.setReviewMsg("reviewMsg");
        request.setFoodStatus(FoodStatus.SHARED);
        FoodCategoryDto foodCategoryDto = new FoodCategoryDto();
        foodCategoryDto.setCategoryName(saveCategory.getName());
        foodCategoryDto.setId(saveCategory.getId());
        request.setFoodCategory(foodCategoryDto);

        // when
        Long saveFoodId = foodService.saveFood(saveUser, request);
        Food food = foodRepository.findById(saveFoodId)
                .orElseThrow(() -> new NotFoundException(""));

        // then
        assertEquals(food.getId(), saveFoodId);
        assertEquals(FoodStatus.SHARED, food.getFoodStatus());
        assertEquals("title", food.getFoodTitle());
        assertEquals("reviewMsg", food.getReviewMsg());
        assertEquals("nickname", food.getWriterNickname());
        assertEquals(1000, food.getPrice());
    }

    @Test
    @DisplayName("food Detail 정보 조회 기능")
    void findFoodDetailTest() throws Exception {
        // given
        User saveUser = saveTestUser("nickname", "name");
        Category saveCategory = saveTestCategory("A");
        List<TagWrapper> tags = new ArrayList<>();
        tags.add(new TagWrapper(saveTag("A"), FoodIngredientType.ADD));
        tags.add(new TagWrapper(saveTag("B"), FoodIngredientType.EXTRACT));
        tags.add(new TagWrapper(saveTag("C"), FoodIngredientType.MAIN));

        Food saveFood = Food.builder()
                .foodTitle("title")
                .price(1000)
                .reviewMsg("reviewMsg")
                .foodStatus(FoodStatus.SHARED)
                .build();
        saveFood.assignCategory(saveCategory);
        saveFood.assignWriter(saveUser);
        saveFood.getFoodTags().addAllTags(tags, saveFood);
        
        // when

        // then
    }
}