package com.yapp.sharefood.food.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.dto.FlavorDto;
import com.yapp.sharefood.flavor.repository.FlavorRepository;
import com.yapp.sharefood.food.domain.*;
import com.yapp.sharefood.food.dto.FoodTagDto;
import com.yapp.sharefood.food.dto.request.FoodCreationRequest;
import com.yapp.sharefood.food.dto.request.FoodTopRankRequest;
import com.yapp.sharefood.food.dto.request.FoodUpdateRequest;
import com.yapp.sharefood.food.dto.request.RecommendationFoodRequest;
import com.yapp.sharefood.food.dto.response.FoodDetailResponse;
import com.yapp.sharefood.food.dto.response.RecommendationFoodResponse;
import com.yapp.sharefood.food.dto.response.TopRankFoodResponse;
import com.yapp.sharefood.food.exception.FoodBanndedException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.like.service.LikeService;
import com.yapp.sharefood.tag.domain.Tag;
import com.yapp.sharefood.tag.repository.TagRepository;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import com.yapp.sharefood.userflavor.domain.UserFlavor;
import com.yapp.sharefood.userflavor.repository.UserFlavorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FoodServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    FoodService foodService;
    @Autowired
    LikeService likeService;

    @Autowired
    FoodRepository foodRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    FlavorRepository flavorRepository;
    @Autowired
    UserFlavorRepository userFlavorRepository;

    private Category saveTestCategory(String categoryName) {
        Category category = Category.of(categoryName);
        return categoryRepository.save(category);
    }

    private Category saveTestCategoryWithParent(String categoryName, Category parent) {
        Category category = Category.of(categoryName);
        category.assignParent(parent);
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

    private Food saveFood(String title, User user, Category category) {
        Food food = Food.builder()
                .foodTitle(title)
                .writer(user)
                .foodStatus(FoodStatus.SHARED)
                .category(category)
                .build();

        return foodRepository.save(food);
    }

    private Food saveFoodWithFlavors(String title, User user, Category category, List<Flavor> flavors) {
        Food food = Food.builder()
                .foodTitle(title)
                .writer(user)
                .foodStatus(FoodStatus.SHARED)
                .category(category)
                .build();
        food.assignFlavors(flavors);

        return foodRepository.save(food);
    }

    private void assignUserFlavor(User user, List<Flavor> flavors) {
        for (Flavor flavor : flavors) {
            userFlavorRepository.save(UserFlavor.of(user, flavor));
        }
    }

    private List<Flavor> findFlavors(List<FlavorType> flavorTypes) {
        return flavorRepository.findByFlavorTypeIsIn(flavorTypes);
    }

    private Tag saveTag(String tagName) {
        return tagRepository.save(Tag.of(tagName));
    }

    @BeforeEach
    void setUp() {
        for (FlavorType flavorType : FlavorType.values()) {
            flavorRepository.save(Flavor.of(flavorType));
        }
    }

    @Test
    @DisplayName("food 내부 값만 저장")
    void saveFoodTest() {
        // given
        User saveUser = saveTestUser("nickname", "name", "oauthId");
        saveTestCategory("A");
        List<TagWrapper> wrapperTags = List.of(
                new TagWrapper(saveTag("tag1"), FoodIngredientType.MAIN),
                new TagWrapper(saveTag("tag2"), FoodIngredientType.ADD),
                new TagWrapper(saveTag("tag3"), FoodIngredientType.EXTRACT));
        List<Flavor> flavors = findFlavors(List.of(FlavorType.SWEET, FlavorType.SPICY, FlavorType.BITTER));

        List<String> flavorDtos = flavors.stream()
                .map(flavor -> flavor.getFlavorType().getFlavorName())
                .collect(Collectors.toList());

        List<FoodTagDto> dtoTags = wrapperTags.stream()
                .map(wrapperTag -> FoodTagDto.of(wrapperTag.getTag().getId(), wrapperTag.getTag().getName(), wrapperTag.getIngredientType()))
                .collect(Collectors.toList());


        FoodCreationRequest request = FoodCreationRequest.builder()
                .title("title")
                .price(1000)
                .reviewMsg("reviewMsg")
                .foodStatus(FoodStatus.SHARED)
                .categoryName("A")
                .tags(dtoTags)
                .flavors(flavorDtos)
                .build();

        // when
        Long saveFoodId = foodService.saveFood(saveUser, request, wrapperTags);
        Food food = foodRepository.findById(saveFoodId)
                .orElseThrow();

        // then
        assertEquals(food.getId(), saveFoodId);
        assertEquals(FoodStatus.SHARED, food.getFoodStatus());
        assertEquals("title", food.getFoodTitle());
        assertEquals("reviewMsg", food.getReviewMsg());
        assertEquals("nickname", food.getWriterNickname());
        assertEquals(FoodStatus.SHARED, food.getFoodStatus());
        assertEquals(1000, food.getPrice());
        assertEquals(3, food.getFoodTags().getFoodTags().size());
        assertEquals(0, food.getLikes().getSize());
        assertEquals(0, food.getNumberOfLikes());
        assertEquals(3, food.getFoodFlavors().getFoodFlavors().size());
    }

    @Test
    @DisplayName("food category 일치 하지 않는 기능 테스트")
    void saveCategoryNotMatchExceptionFoodTest() {
        // given
        User saveUser = saveTestUser("nickname", "name", "oauthId");
        saveTestCategory("A");
        List<TagWrapper> wrapperTags = List.of(
                new TagWrapper(saveTag("tag1"), FoodIngredientType.MAIN),
                new TagWrapper(saveTag("tag2"), FoodIngredientType.ADD),
                new TagWrapper(saveTag("tag3"), FoodIngredientType.EXTRACT));
        List<Flavor> flavors = findFlavors(List.of(FlavorType.SWEET, FlavorType.SPICY, FlavorType.BITTER));

        List<String> flavorDtos = flavors.stream()
                .map(flavor -> flavor.getFlavorType().getFlavorName())
                .collect(Collectors.toList());

        List<FoodTagDto> dtoTags = wrapperTags.stream()
                .map(wrapperTag -> FoodTagDto.of(wrapperTag.getTag().getId(), wrapperTag.getTag().getName(), wrapperTag.getIngredientType()))
                .collect(Collectors.toList());

        FoodCreationRequest request = FoodCreationRequest.builder()
                .title("title")
                .price(1000)
                .reviewMsg("reviewMsg")
                .foodStatus(FoodStatus.SHARED)
                .categoryName("B")
                .tags(dtoTags)
                .flavors(flavorDtos)
                .build();

        // when

        // then
        assertThrows(CategoryNotFoundException.class, () -> foodService.saveFood(saveUser, request, wrapperTags));
    }

    @Test
    @DisplayName("food Detail 정보 조회 기능")
    void findFoodDetailTest() throws Exception {
        // given
        User saveUser = saveTestUser("nickname", "name", "oauthId");
        Category saveCategory = saveTestCategory("A");
        List<TagWrapper> tags = new ArrayList<>();
        tags.add(new TagWrapper(saveTag("A"), FoodIngredientType.ADD));
        tags.add(new TagWrapper(saveTag("B"), FoodIngredientType.EXTRACT));
        tags.add(new TagWrapper(saveTag("C"), FoodIngredientType.MAIN));

        Food newFood = Food.builder()
                .foodTitle("title")
                .price(1000)
                .reviewMsg("reviewMsg")
                .foodStatus(FoodStatus.SHARED)
                .writer(saveUser)
                .category(saveCategory)
                .build();
        newFood.getFoodTags().addAllTags(tags, newFood);
        Food saveFood = foodRepository.save(newFood);

        // when
        FoodDetailResponse foodResponse = foodService.findFoodDetailById(saveUser, saveFood.getId());

        // then
        assertEquals("title", foodResponse.getFoodTitle());
        assertEquals(1000, foodResponse.getPrice());
        assertEquals("reviewMsg", foodResponse.getReviewDetail());
        assertEquals("nickname", foodResponse.getWriterName());
        assertFalse(foodResponse.isMeBookmark());
        assertFalse(foodResponse.isMeLike());
        assertEquals(3, foodResponse.getFoodTags().size());
    }

    @Test
    @DisplayName("food Detail 정보 조회 실패 - 정지된 게시글")
    void findFoodDetailTest_Fail_Bannded() throws Exception {
        // given
        User saveUser = saveTestUser("nickname", "name", "oauthId");
        Category saveCategory = saveTestCategory("A");
        List<TagWrapper> tags = new ArrayList<>();
        tags.add(new TagWrapper(saveTag("A"), FoodIngredientType.ADD));
        tags.add(new TagWrapper(saveTag("B"), FoodIngredientType.EXTRACT));
        tags.add(new TagWrapper(saveTag("C"), FoodIngredientType.MAIN));

        Food newFood = Food.builder()
                .foodTitle("title")
                .price(1000)
                .reviewMsg("reviewMsg")
                .foodStatus(FoodStatus.SHARED)
                .writer(saveUser)
                .category(saveCategory)
                .build();
        newFood.getFoodTags().addAllTags(tags, newFood);
        Food saveFood = foodRepository.save(newFood);
        saveFood.addReport(FoodReportType.POSTING_OBSCENE_CONTENT.getMessage());

        // when

        // then
        assertThrows(FoodBanndedException.class, () -> foodService.findFoodDetailById(saveUser, saveFood.getId()));
    }

    @Test
    @DisplayName("food rank 조회 기능")
    void findTopRankFoodsTest() throws Exception {
        // given
        FoodTopRankRequest foodTopRankRequest = FoodTopRankRequest.of(4, 7, "A");
        Category saveCategory = saveTestCategory("A");
        User user1 = saveTestUser("user1_nick", "user1_name", "oauthId1");
        User user2 = saveTestUser("user2_nick", "user2_name", "oauthId2");
        User user3 = saveTestUser("user3_nick", "user3_name", "oauthId3");
        User user4 = saveTestUser("user4_nick", "user4_name", "oauthId4");
        User user5 = saveTestUser("user5_nick", "user5_name", "oauthId5");

        Food food1 = saveFood("food title1", user1, saveCategory);
        Food food2 = saveFood("food title2", user1, saveCategory);
        Food food3 = saveFood("food title3", user1, saveCategory);
        Food food4 = saveFood("food title4", user1, saveCategory);
        likeService.saveLike(user2, food1.getId(), saveCategory.getName());
        likeService.saveLike(user3, food1.getId(), saveCategory.getName());
        likeService.saveLike(user4, food1.getId(), saveCategory.getName());
        likeService.saveLike(user5, food1.getId(), saveCategory.getName());

        likeService.saveLike(user3, food3.getId(), saveCategory.getName());
        likeService.saveLike(user4, food3.getId(), saveCategory.getName());
        likeService.saveLike(user5, food3.getId(), saveCategory.getName());

        likeService.saveLike(user4, food2.getId(), saveCategory.getName());
        likeService.saveLike(user5, food2.getId(), saveCategory.getName());

        likeService.saveLike(user5, food4.getId(), saveCategory.getName());

        // when
        TopRankFoodResponse topRankFoods = foodService.findTopRankFoods(foodTopRankRequest, user1);

        // then
        assertEquals(4, topRankFoods.getTopRankingFoods().size());
        assertEquals("food title1", topRankFoods.getTopRankingFoods().get(0).getFoodTitle());
        assertEquals("food title3", topRankFoods.getTopRankingFoods().get(1).getFoodTitle());
        assertEquals("food title2", topRankFoods.getTopRankingFoods().get(2).getFoodTitle());
        assertEquals("food title4", topRankFoods.getTopRankingFoods().get(3).getFoodTitle());
    }

    @Test
    @DisplayName("food rank 조회 기능 Child category 까지 조회")
    void findTopRankWithChildrensFoodsTest() throws Exception {
        // given
        FoodTopRankRequest foodTopRankRequest = FoodTopRankRequest.of(4, 7, "A");
        Category saveCategoryParent = saveTestCategory("A");
        Category saveCategoryChild = saveTestCategoryWithParent("B", saveCategoryParent);
        User user1 = saveTestUser("user1_nick", "user1_name", "oauthId1");
        User user2 = saveTestUser("user2_nick", "user2_name", "oauthId2");
        User user3 = saveTestUser("user3_nick", "user3_name", "oauthId3");
        User user4 = saveTestUser("user4_nick", "user4_name", "oauthId4");
        User user5 = saveTestUser("user5_nick", "user5_name", "oauthId5");

        Food food1 = saveFood("food title1", user1, saveCategoryChild);
        Food food2 = saveFood("food title2", user1, saveCategoryChild);
        Food food3 = saveFood("food title3", user1, saveCategoryChild);
        Food food4 = saveFood("food title4", user1, saveCategoryChild);
        likeService.saveLike(user2, food1.getId(), saveCategoryChild.getName());
        likeService.saveLike(user3, food1.getId(), saveCategoryChild.getName());
        likeService.saveLike(user4, food1.getId(), saveCategoryChild.getName());
        likeService.saveLike(user5, food1.getId(), saveCategoryChild.getName());

        likeService.saveLike(user3, food3.getId(), saveCategoryChild.getName());
        likeService.saveLike(user4, food3.getId(), saveCategoryChild.getName());
        likeService.saveLike(user5, food3.getId(), saveCategoryChild.getName());

        likeService.saveLike(user4, food2.getId(), saveCategoryChild.getName());
        likeService.saveLike(user5, food2.getId(), saveCategoryChild.getName());

        likeService.saveLike(user5, food4.getId(), saveCategoryChild.getName());

        // when
        TopRankFoodResponse topRankFoods = foodService.findTopRankFoods(foodTopRankRequest, user1);

        // then
        assertEquals(4, topRankFoods.getTopRankingFoods().size());
        assertEquals("food title1", topRankFoods.getTopRankingFoods().get(0).getFoodTitle());
        assertEquals("food title3", topRankFoods.getTopRankingFoods().get(1).getFoodTitle());
        assertEquals("food title2", topRankFoods.getTopRankingFoods().get(2).getFoodTitle());
        assertEquals("food title4", topRankFoods.getTopRankingFoods().get(3).getFoodTitle());
    }

    @Test
    @DisplayName("food rank 조회 기능 카테고리 범위가 아닌 경우 테스트")
    void findTopRankNotAssociatedCategoryFoodsTest() throws Exception {
        // given
        FoodTopRankRequest foodTopRankRequest = FoodTopRankRequest.of(4, 7, "A");
        Category saveExternalCategoryParent = saveTestCategory("external");
        saveTestCategory("A");
        User user1 = saveTestUser("user1_nick", "user1_name", "oauthId1");
        User user2 = saveTestUser("user2_nick", "user2_name", "oauthId2");
        User user3 = saveTestUser("user3_nick", "user3_name", "oauthId3");
        User user4 = saveTestUser("user4_nick", "user4_name", "oauthId4");
        User user5 = saveTestUser("user5_nick", "user5_name", "oauthId5");

        Food food1 = saveFood("food title1", user1, saveExternalCategoryParent);
        Food food2 = saveFood("food title2", user1, saveExternalCategoryParent);
        Food food3 = saveFood("food title3", user1, saveExternalCategoryParent);
        Food food4 = saveFood("food title4", user1, saveExternalCategoryParent);
        likeService.saveLike(user2, food1.getId(), saveExternalCategoryParent.getName());
        likeService.saveLike(user3, food1.getId(), saveExternalCategoryParent.getName());
        likeService.saveLike(user4, food1.getId(), saveExternalCategoryParent.getName());
        likeService.saveLike(user5, food1.getId(), saveExternalCategoryParent.getName());

        likeService.saveLike(user3, food3.getId(), saveExternalCategoryParent.getName());
        likeService.saveLike(user4, food3.getId(), saveExternalCategoryParent.getName());
        likeService.saveLike(user5, food3.getId(), saveExternalCategoryParent.getName());

        likeService.saveLike(user4, food2.getId(), saveExternalCategoryParent.getName());
        likeService.saveLike(user5, food2.getId(), saveExternalCategoryParent.getName());

        likeService.saveLike(user5, food4.getId(), saveExternalCategoryParent.getName());

        // when
        TopRankFoodResponse topRankFoods = foodService.findTopRankFoods(foodTopRankRequest, user1);

        // then
        assertEquals(0, topRankFoods.getTopRankingFoods().size());
    }

    @Test
    @DisplayName("food rank 조회할 때 전체 food 보다 작은 걍우")
    void findRankFoodLessThanAllTest() throws Exception {
        FoodTopRankRequest foodTopRankRequest = FoodTopRankRequest.of(4, 7, "A");
        Category saveCategory = saveTestCategory("A");
        User user1 = saveTestUser("user1_nick", "user1_name", "oauthId1");
        User user2 = saveTestUser("user2_nick", "user2_name", "oauthId2");
        User user3 = saveTestUser("user3_nick", "user3_name", "oauthId3");
        User user4 = saveTestUser("user4_nick", "user4_name", "oauthId4");
        User user5 = saveTestUser("user5_nick", "user5_name", "oauthId5");

        Food food1 = saveFood("food title1", user1, saveCategory);
        Food food2 = saveFood("food title2", user1, saveCategory);
        Food food3 = saveFood("food title3", user1, saveCategory);
        Food food4 = saveFood("food title4", user1, saveCategory);
        likeService.saveLike(user2, food1.getId(), saveCategory.getName());
        likeService.saveLike(user3, food1.getId(), saveCategory.getName());
        likeService.saveLike(user4, food1.getId(), saveCategory.getName());
        likeService.saveLike(user5, food1.getId(), saveCategory.getName());

        likeService.saveLike(user3, food3.getId(), saveCategory.getName());
        likeService.saveLike(user4, food3.getId(), saveCategory.getName());
        likeService.saveLike(user5, food3.getId(), saveCategory.getName());

        likeService.saveLike(user4, food2.getId(), saveCategory.getName());
        likeService.saveLike(user5, food2.getId(), saveCategory.getName());

        likeService.saveLike(user5, food4.getId(), saveCategory.getName());

        // when
        TopRankFoodResponse topRankFoods = foodService.findTopRankFoods(foodTopRankRequest, user1);

        // then
        assertEquals(4, topRankFoods.getTopRankingFoods().size());
        assertEquals("food title1", topRankFoods.getTopRankingFoods().get(0).getFoodTitle());
        assertEquals("food title3", topRankFoods.getTopRankingFoods().get(1).getFoodTitle());
        assertEquals("food title2", topRankFoods.getTopRankingFoods().get(2).getFoodTitle());
        assertEquals("food title4", topRankFoods.getTopRankingFoods().get(3).getFoodTitle());
    }

    @Test
    @DisplayName("food rank 조회 기능 0개의 like를 가진 food는 재외하는 경우 테스트")
    void findFoodRankIfZeroLikeExist() throws Exception {
        // given
        FoodTopRankRequest foodTopRankRequest = FoodTopRankRequest.of(4, 7, "A");
        Category saveCategory = saveTestCategory("A");
        User user1 = saveTestUser("user1_nick", "user1_name", "oauthId1");
        User user2 = saveTestUser("user2_nick", "user2_name", "oauthId2");
        User user3 = saveTestUser("user3_nick", "user3_name", "oauthId3");
        User user4 = saveTestUser("user4_nick", "user4_name", "oauthId4");
        User user5 = saveTestUser("user5_nick", "user5_name", "oauthId5");

        Food food1 = saveFood("food title1", user1, saveCategory);
        Food food2 = saveFood("food title2", user1, saveCategory);
        Food food3 = saveFood("food title3", user1, saveCategory);
        Food food4 = saveFood("food title4", user1, saveCategory);
        likeService.saveLike(user2, food1.getId(), saveCategory.getName());
        likeService.saveLike(user3, food1.getId(), saveCategory.getName());
        likeService.saveLike(user4, food1.getId(), saveCategory.getName());
        likeService.saveLike(user5, food1.getId(), saveCategory.getName());

        likeService.saveLike(user3, food3.getId(), saveCategory.getName());
        likeService.saveLike(user4, food3.getId(), saveCategory.getName());
        likeService.saveLike(user5, food3.getId(), saveCategory.getName());

        likeService.saveLike(user4, food2.getId(), saveCategory.getName());
        likeService.saveLike(user5, food2.getId(), saveCategory.getName());

        likeService.saveLike(user5, food4.getId(), saveCategory.getName());

        // when
        TopRankFoodResponse topRankFoods = foodService.findTopRankFoods(foodTopRankRequest, user1);

        // then
        assertEquals(4, topRankFoods.getTopRankingFoods().size());
        assertEquals("food title1", topRankFoods.getTopRankingFoods().get(0).getFoodTitle());
        assertEquals("food title3", topRankFoods.getTopRankingFoods().get(1).getFoodTitle());
        assertEquals("food title2", topRankFoods.getTopRankingFoods().get(2).getFoodTitle());
        assertEquals("food title4", topRankFoods.getTopRankingFoods().get(3).getFoodTitle());
    }


    @Test
    @DisplayName("food 추천 기능 테스트")
    void foodRecommendataionTest() throws Exception {
        // given
        RecommendationFoodRequest request = RecommendationFoodRequest.of(4, 7, "A");
        List<Flavor> flavors = findFlavors(List.of(FlavorType.BITTER));

        Category saveCategory = saveTestCategory("A");
        User user1 = saveTestUser("user1_nick", "user1_name", "oauthId1");
        assignUserFlavor(user1, flavors);

        User user2 = saveTestUser("user2_nick", "user2_name", "oauthId2");
        User user3 = saveTestUser("user3_nick", "user3_name", "oauthId3");
        User user4 = saveTestUser("user4_nick", "user4_name", "oauthId4");
        User user5 = saveTestUser("user5_nick", "user5_name", "oauthId5");

        Food food1 = saveFoodWithFlavors("food title1", user1, saveCategory, findFlavors(List.of(FlavorType.BITTER, FlavorType.SWEET)));
        Food food2 = saveFoodWithFlavors("food title2", user1, saveCategory, findFlavors(List.of(FlavorType.BITTER, FlavorType.COOL_DETAIL)));
        Food food3 = saveFoodWithFlavors("food title3", user1, saveCategory, findFlavors(List.of(FlavorType.BITTER, FlavorType.REFRESH_DETAIL)));
        Food food4 = saveFoodWithFlavors("food title4", user1, saveCategory, findFlavors(List.of(FlavorType.SOUR)));
        likeService.saveLike(user2, food1.getId(), saveCategory.getName());
        likeService.saveLike(user3, food1.getId(), saveCategory.getName());
        likeService.saveLike(user4, food1.getId(), saveCategory.getName());
        likeService.saveLike(user5, food1.getId(), saveCategory.getName());

        likeService.saveLike(user3, food3.getId(), saveCategory.getName());
        likeService.saveLike(user4, food3.getId(), saveCategory.getName());
        likeService.saveLike(user5, food3.getId(), saveCategory.getName());

        likeService.saveLike(user4, food2.getId(), saveCategory.getName());
        likeService.saveLike(user5, food2.getId(), saveCategory.getName());

        likeService.saveLike(user5, food4.getId(), saveCategory.getName());

        // when
        RecommendationFoodResponse foodRecommendation = foodService.findFoodRecommendation(request, user1);

        // then
        assertEquals(3, foodRecommendation.getRecommendationFoods().size());
    }

    @Test
    @DisplayName("food 추천 기능 User가 flavor설정 하지 않은 경우 테스트")
    void foodRecommendataionUserFlavorIsEmptyTest_Success() throws Exception {
        // given
        RecommendationFoodRequest request = RecommendationFoodRequest.of(4, 7, "A");

        Category saveCategory = saveTestCategory("A");
        User user1 = saveTestUser("user1_nick", "user1_name", "oauthId1");

        User user2 = saveTestUser("user2_nick", "user2_name", "oauthId2");
        User user3 = saveTestUser("user3_nick", "user3_name", "oauthId3");
        User user4 = saveTestUser("user4_nick", "user4_name", "oauthId4");
        User user5 = saveTestUser("user5_nick", "user5_name", "oauthId5");

        Food food1 = saveFoodWithFlavors("food title1", user1, saveCategory, findFlavors(List.of(FlavorType.BITTER, FlavorType.SWEET)));
        Food food2 = saveFoodWithFlavors("food title2", user1, saveCategory, findFlavors(List.of(FlavorType.BITTER, FlavorType.COOL_DETAIL)));
        Food food3 = saveFoodWithFlavors("food title3", user1, saveCategory, findFlavors(List.of(FlavorType.BITTER, FlavorType.REFRESH_DETAIL)));
        Food food4 = saveFoodWithFlavors("food title4", user1, saveCategory, findFlavors(List.of(FlavorType.BITTER, FlavorType.SOUR)));
        likeService.saveLike(user2, food1.getId(), saveCategory.getName());
        likeService.saveLike(user3, food1.getId(), saveCategory.getName());
        likeService.saveLike(user4, food1.getId(), saveCategory.getName());
        likeService.saveLike(user5, food1.getId(), saveCategory.getName());

        likeService.saveLike(user3, food3.getId(), saveCategory.getName());
        likeService.saveLike(user4, food3.getId(), saveCategory.getName());
        likeService.saveLike(user5, food3.getId(), saveCategory.getName());

        likeService.saveLike(user4, food2.getId(), saveCategory.getName());
        likeService.saveLike(user5, food2.getId(), saveCategory.getName());

        likeService.saveLike(user5, food4.getId(), saveCategory.getName());

        // when
        RecommendationFoodResponse foodRecommendation = foodService.findFoodRecommendation(request, user1);

        // then
        assertEquals(4, foodRecommendation.getRecommendationFoods().size());
    }

    @Test
    @DisplayName("food update 기능 테스트 - 성공")
    void updateFood_Success() throws Exception {
        // given
        User user = saveTestUser("userNickname", "name", "123124");
        Category category = saveTestCategory("샌드위치");
        saveTestCategory("마라탕");
        List<TagWrapper> tagWrappers = new ArrayList<>();

        Food food = Food.builder()
                .foodTitle("title0")
                .reviewMsg("review msg before")
                .price(1000)
                .category(category)
                .foodStatus(FoodStatus.MINE)
                .writer(user)
                .build();
        food.assignWrapperTags(tagWrappers);
        foodRepository.save(food);
        List<Tag> beforeSaveTags = List.of(Tag.of("재료1"), Tag.of("재료2"), Tag.of("재료3"));
        List<Tag> saveTags = tagRepository.saveAll(beforeSaveTags);
        List<TagWrapper> newTagWrapper = List.of(
                new TagWrapper(saveTags.get(0), FoodIngredientType.MAIN),
                new TagWrapper(saveTags.get(1), FoodIngredientType.ADD),
                new TagWrapper(saveTags.get(2), FoodIngredientType.EXTRACT));

        // when
        FoodUpdateRequest foodUpdateRequest = FoodUpdateRequest.builder()
                .categoryName("커피")
                .title("title1")
                .reviewMsg("reviewDetail")
                .price(1000)
                .categoryName("마라탕")
                .foodStatus(FoodStatus.SHARED)
                .tags(List.of(FoodTagDto.of(null, "재료1", FoodIngredientType.MAIN),
                        FoodTagDto.of(null, "재료2", FoodIngredientType.ADD),
                        FoodTagDto.of(null, "재료3", FoodIngredientType.EXTRACT)))
                .flavors(List.of(FlavorDto.of(1L, FlavorType.SWEET), FlavorDto.of(2L, FlavorType.SOUR), FlavorDto.of(3L, FlavorType.PLAIN_DETAIL)))
                .build();


        // then
        FoodDetailResponse foodDetailResponse = foodService.updateFood(user, food.getId(), newTagWrapper, foodUpdateRequest);

        assertEquals("title1", foodDetailResponse.getFoodTitle());
        assertEquals("reviewDetail", foodDetailResponse.getReviewDetail());
        assertThat(foodUpdateRequest.getTags())
                .hasSize(3)
                .extracting("name")
                .containsExactlyInAnyOrderElementsOf(List.of("재료1", "재료2", "재료3"));
        assertThat(foodUpdateRequest.getFlavors())
                .hasSize(3)
                .extracting("flavorName")
                .containsExactlyInAnyOrderElementsOf(List.of("단맛", "씬맛", "단백한"));
    }
}