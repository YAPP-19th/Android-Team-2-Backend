package com.yapp.sharefood.food.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.common.service.IntegrationService;
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
import com.yapp.sharefood.user.domain.Grade;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import com.yapp.sharefood.userflavor.domain.UserFlavor;
import com.yapp.sharefood.userflavor.repository.UserFlavorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class FoodServiceTest extends IntegrationService {

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
    @DisplayName("food ?????? ?????? ??????")
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
        assertEquals(saveUser.getGradePoint(), Grade.POINT_OPEN_FOOD + Grade.POINT_REGISTER_FOOD);
    }

    @Test
    @DisplayName("food category ?????? ?????? ?????? ?????? ?????????")
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
    @DisplayName("food Detail ?????? ?????? ??????")
    void findFoodDetailTest() {
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
    @DisplayName("food Detail ?????? ?????? ?????? - ????????? ?????????")
    void findFoodDetailTest_Fail_Bannded() {
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
    @DisplayName("food rank ?????? ??????")
    void findTopRankFoodsTest() {
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
        likeService.saveLike(user2, food1.getId());
        likeService.saveLike(user3, food1.getId());
        likeService.saveLike(user4, food1.getId());
        likeService.saveLike(user5, food1.getId());

        likeService.saveLike(user3, food3.getId());
        likeService.saveLike(user4, food3.getId());
        likeService.saveLike(user5, food3.getId());

        likeService.saveLike(user4, food2.getId());
        likeService.saveLike(user5, food2.getId());

        likeService.saveLike(user5, food4.getId());

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
    @DisplayName("food rank ?????? ?????? Child category ?????? ??????")
    void findTopRankWithChildrensFoodsTest() {
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
        likeService.saveLike(user2, food1.getId());
        likeService.saveLike(user3, food1.getId());
        likeService.saveLike(user4, food1.getId());
        likeService.saveLike(user5, food1.getId());

        likeService.saveLike(user3, food3.getId());
        likeService.saveLike(user4, food3.getId());
        likeService.saveLike(user5, food3.getId());

        likeService.saveLike(user4, food2.getId());
        likeService.saveLike(user5, food2.getId());

        likeService.saveLike(user5, food4.getId());

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
    @DisplayName("food rank ?????? ?????? ???????????? ????????? ?????? ?????? ?????????")
    void findTopRankNotAssociatedCategoryFoodsTest() {
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
        likeService.saveLike(user2, food1.getId());
        likeService.saveLike(user3, food1.getId());
        likeService.saveLike(user4, food1.getId());
        likeService.saveLike(user5, food1.getId());

        likeService.saveLike(user3, food3.getId());
        likeService.saveLike(user4, food3.getId());
        likeService.saveLike(user5, food3.getId());

        likeService.saveLike(user4, food2.getId());
        likeService.saveLike(user5, food2.getId());

        likeService.saveLike(user5, food4.getId());

        // when
        TopRankFoodResponse topRankFoods = foodService.findTopRankFoods(foodTopRankRequest, user1);

        // then
        assertEquals(0, topRankFoods.getTopRankingFoods().size());
    }

    @Test
    @DisplayName("food rank ????????? ??? ?????? food ?????? ?????? ??????")
    void findRankFoodLessThanAllTest() {
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
        likeService.saveLike(user2, food1.getId());
        likeService.saveLike(user3, food1.getId());
        likeService.saveLike(user4, food1.getId());
        likeService.saveLike(user5, food1.getId());

        likeService.saveLike(user3, food3.getId());
        likeService.saveLike(user4, food3.getId());
        likeService.saveLike(user5, food3.getId());

        likeService.saveLike(user4, food2.getId());
        likeService.saveLike(user5, food2.getId());

        likeService.saveLike(user5, food4.getId());

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
    @DisplayName("food rank ?????? ?????? 0?????? like??? ?????? food??? ???????????? ?????? ?????????")
    void findFoodRankIfZeroLikeExist() {
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
        likeService.saveLike(user2, food1.getId());
        likeService.saveLike(user3, food1.getId());
        likeService.saveLike(user4, food1.getId());
        likeService.saveLike(user5, food1.getId());

        likeService.saveLike(user3, food3.getId());
        likeService.saveLike(user4, food3.getId());
        likeService.saveLike(user5, food3.getId());

        likeService.saveLike(user4, food2.getId());
        likeService.saveLike(user5, food2.getId());

        likeService.saveLike(user5, food4.getId());

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
    @DisplayName("food ?????? ?????? ?????????")
    void foodRecommendataionTest() {
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
        likeService.saveLike(user2, food1.getId());
        likeService.saveLike(user3, food1.getId());
        likeService.saveLike(user4, food1.getId());
        likeService.saveLike(user5, food1.getId());

        likeService.saveLike(user3, food3.getId());
        likeService.saveLike(user4, food3.getId());
        likeService.saveLike(user5, food3.getId());

        likeService.saveLike(user4, food2.getId());
        likeService.saveLike(user5, food2.getId());

        likeService.saveLike(user5, food4.getId());

        // when
        RecommendationFoodResponse foodRecommendation = foodService.findFoodRecommendation(request, user1);

        // then
        assertEquals(3, foodRecommendation.getRecommendationFoods().size());
    }

    @Test
    @DisplayName("food ?????? ?????? User??? flavor?????? ?????? ?????? ?????? ?????????")
    void foodRecommendataionUserFlavorIsEmptyTest_Success() {
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
        likeService.saveLike(user2, food1.getId());
        likeService.saveLike(user3, food1.getId());
        likeService.saveLike(user4, food1.getId());
        likeService.saveLike(user5, food1.getId());

        likeService.saveLike(user3, food3.getId());
        likeService.saveLike(user4, food3.getId());
        likeService.saveLike(user5, food3.getId());

        likeService.saveLike(user4, food2.getId());
        likeService.saveLike(user5, food2.getId());

        likeService.saveLike(user5, food4.getId());

        // when
        RecommendationFoodResponse foodRecommendation = foodService.findFoodRecommendation(request, user1);

        // then
        assertEquals(4, foodRecommendation.getRecommendationFoods().size());
    }

    @Test
    @DisplayName("food update ?????? ????????? - ??????")
    void updateFood_Success() {
        // given
        User user = saveTestUser("userNickname", "name", "123124");
        Category category = saveTestCategory("????????????");
        saveTestCategory("?????????");
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
        List<Tag> beforeSaveTags = List.of(Tag.of("??????1"), Tag.of("??????2"), Tag.of("??????3"));
        List<Tag> saveTags = tagRepository.saveAll(beforeSaveTags);
        List<TagWrapper> newTagWrapper = List.of(
                new TagWrapper(saveTags.get(0), FoodIngredientType.MAIN),
                new TagWrapper(saveTags.get(1), FoodIngredientType.ADD),
                new TagWrapper(saveTags.get(2), FoodIngredientType.EXTRACT));

        // when
        FoodUpdateRequest foodUpdateRequest = FoodUpdateRequest.builder()
                .categoryName("??????")
                .title("title1")
                .reviewMsg("reviewDetail")
                .price(1000)
                .categoryName("?????????")
                .foodStatus(FoodStatus.SHARED)
                .tags(List.of(FoodTagDto.of(null, "??????1", FoodIngredientType.MAIN),
                        FoodTagDto.of(null, "??????2", FoodIngredientType.ADD),
                        FoodTagDto.of(null, "??????3", FoodIngredientType.EXTRACT)))
                .flavors(List.of(FlavorDto.of(1L, FlavorType.SWEET), FlavorDto.of(2L, FlavorType.SOUR), FlavorDto.of(3L, FlavorType.PLAIN_DETAIL)))
                .build();


        // then
        FoodDetailResponse foodDetailResponse = foodService.updateFood(user, food.getId(), newTagWrapper, foodUpdateRequest);

        assertEquals("title1", foodDetailResponse.getFoodTitle());
        assertEquals("reviewDetail", foodDetailResponse.getReviewDetail());
        assertThat(foodUpdateRequest.getTags())
                .hasSize(3)
                .extracting("name")
                .containsExactlyInAnyOrderElementsOf(List.of("??????1", "??????2", "??????3"));
        assertThat(foodUpdateRequest.getFlavors())
                .hasSize(3)
                .extracting("flavorName")
                .containsExactlyInAnyOrderElementsOf(List.of("??????", "??????", "?????????"));
    }
}