package com.yapp.sharefood.food.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.repository.FlavorRepository;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodFlavor;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.dto.FoodPageDto;
import com.yapp.sharefood.food.dto.request.FoodPageSearchRequest;
import com.yapp.sharefood.food.dto.response.FoodPageResponse;
import com.yapp.sharefood.food.repository.FoodFlavorRepository;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.like.domain.Like;
import com.yapp.sharefood.like.repository.LikeRepository;
import com.yapp.sharefood.like.service.LikeService;
import com.yapp.sharefood.tag.domain.Tag;
import com.yapp.sharefood.tag.repository.TagRepository;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import com.yapp.sharefood.userflavor.repository.UserFlavorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class FoodFindPageTest {

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
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    FoodFlavorRepository foodFlavorRepository;

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

    private User ownerUser;
    private User otherUser;
    private Category category;
    private List<Food> foods;
    private List<Flavor> flavors;
    private List<Tag> tags;

    @BeforeEach
    void init() {
        this.flavors = setUpFlavors();
        this.tags = setUpTags();
        this.category = saveTestCategory("category");
        this.ownerUser = saveTestUser("owner_nickname", "owner_name", "oauthId");
        this.otherUser = saveTestUser("other_nickanem", "other_name", "other_oauth_id");

        this.foods = this.initFoods();

        List<User> users = initUser();
        initLike(this.foods, users);

        em.flush();
        em.clear();
    }

    private List<Tag> setUpTags() {
        List<Tag> tags = new ArrayList<>();
        Tag tag1 = Tag.of("카푸치노");
        Tag tag2 = Tag.of("시럽");
        Tag tag3 = Tag.of("샷추가");
        Tag tag4 = Tag.of("크림");
        tags.add(tagRepository.save(tag1));
        tags.add(tagRepository.save(tag2));
        tags.add(tagRepository.save(tag3));
        tags.add(tagRepository.save(tag4));

        return tags;
    }

    private List<User> initUser() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(User.builder()
                    .nickname("nickname" + i)
                    .name("name" + i)
                    .oAuthType(OAuthType.KAKAO)
                    .oauthId("oauth_id" + i)
                    .build());
        }

        return userRepository.saveAll(users);
    }

    private Food findFoodById(Long id) {
        return this.foods.stream().filter(food -> Objects.equals(food.getId(), id))
                .findAny()
                .orElseThrow();
    }

    private List<Flavor> setUpFlavors() {
        List<Flavor> flavors = new ArrayList<>();
        for (FlavorType flavorType : FlavorType.values()) {
            flavors.add(flavorRepository.save(Flavor.of(flavorType)));
        }

        return flavors;
    }

    private List<Food> initFoods() {
        List<Food> foods = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Food food = Food.builder()
                    .foodTitle("title_" + i)
                    .foodStatus(FoodStatus.SHARED)
                    .category(this.category)
                    .price(i)
                    .reviewMsg("review")
                    .writer(this.ownerUser)
                    .build();
            foods.add(food);
        }
        foodRepository.saveAll(foods);

        return foods;
    }

    private void initLike(List<Food> foods, List<User> users) {
        if (foods.size() != users.size()) {
            throw new InvalidOperationException("user food size 다름");
        }
        List<Like> likes = new ArrayList<>();
        for (int i = 0; i < foods.size(); i++) {
            for (int j = 0; j < i; j++) {
                Like like = Like.of(users.get(j));
                foods.get(i).assignLike(like);
                likes.add(like);
            }
        }

        likeRepository.saveAll(likes);
    }

    private Flavor findFlavor(FlavorType flavorType) {
        return flavors.stream()
                .filter(flavor -> flavor.getFlavorType() == flavorType)
                .findAny()
                .orElseThrow();
    }


    @Test
    @DisplayName("조건 없이 food 조회")
    void foodPageSearchNormalTest_Success() throws Exception {
        // given
        FoodPageSearchRequest foodPageSearchRequest = FoodPageSearchRequest
                .builder()
                .sort("id")
                .order("desc")
                .categoryName("category")
                .offset(0L)
                .pageSize(5)
                .tags(new ArrayList<>())
                .flavors(new ArrayList<>())
                .firstSearchTime(LocalDateTime.now())
                .build();

        // when
        FoodPageResponse foodPageResponse = foodService.searchFoodsPage(foodPageSearchRequest);

        // then
        assertEquals(5, foodPageResponse.getPageSize());
        assertEquals(0L, foodPageResponse.getOffset());
        assertThat(foodPageResponse.getFoods())
                .hasSize(5);
        Long lastFoodId = foods.get(foods.size() - 1).getId();
        assertThat(lastFoodId).isNotNull();
        FoodPageDto lastSearchFood = foodPageResponse.getFoods().get(foodPageResponse.getFoods().size() - 1);
        assertEquals(lastFoodId, lastSearchFood.getId());
    }

    @MethodSource
    @ParameterizedTest(name = "price로 filter를 정한 경우 id로 order - 성공")
    void foodPageSearchByPriceTest_Success(Integer minPrice, Integer maxPrice, long expectedPageOffset, int expectedPageSize, List<String> extractTitles) throws Exception {
        // given
        FoodPageSearchRequest foodPageSearchRequest = FoodPageSearchRequest
                .builder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .sort("id")
                .order("desc")
                .categoryName("category")
                .offset(0L)
                .pageSize(5)
                .tags(new ArrayList<>())
                .flavors(new ArrayList<>())
                .firstSearchTime(LocalDateTime.now())
                .build();

        // when
        FoodPageResponse foodPageResponse = foodService.searchFoodsPage(foodPageSearchRequest);

        // then
        assertEquals(expectedPageSize, foodPageResponse.getPageSize());
        assertEquals(expectedPageOffset, foodPageResponse.getOffset());
        assertThat(foodPageResponse.getFoods())
                .isNotNull()
                .extracting("foodTitle")
                .containsExactlyInAnyOrderElementsOf(extractTitles);
    }

    static Stream<Arguments> foodPageSearchByPriceTest_Success() {
        return Stream.of(
                Arguments.of(null, null, 0L, 5, List.of("title_9", "title_8", "title_7", "title_6", "title_5")),
                Arguments.of(null, 8, 0L, 5, List.of("title_8", "title_7", "title_6", "title_5", "title_4")),
                Arguments.of(7, null, -1L, 5, List.of("title_9", "title_8", "title_7")),
                Arguments.of(2, 4, -1L, 5, List.of("title_4", "title_3", "title_2"))
        );
    }

    @Test
    @DisplayName("Tag와 Flavor 로 동시에 조회할 경우 에러 발생")
    void foodFlavorTagSameRequset_Exception() throws Exception {
        // given
        List<String> tagRequset = new ArrayList<>();
        for (Tag tag : this.tags) {
            tagRequset.add(tag.getName());
        }
        List<String> flavorsRequest = new ArrayList<>();
        for (Flavor flavor : this.flavors) {
            flavorsRequest.add(flavor.getFlavorType().getFlavorName());
        }

        FoodPageSearchRequest foodPageSearchRequest = FoodPageSearchRequest
                .builder()
                .minPrice(null)
                .maxPrice(null)
                .sort("id")
                .order("desc")
                .categoryName("category")
                .offset(0L)
                .pageSize(5)
                .tags(tagRequset)
                .flavors(flavorsRequest)
                .firstSearchTime(LocalDateTime.now())
                .build();

        // when

        // then
        assertThrows(InvalidOperationException.class, () -> foodService.searchFoodsPage(foodPageSearchRequest));
    }

    @MethodSource
    @ParameterizedTest(name = "food flavor 로 조회한 케이스 테스트")
    void foodSearchWithFlavorsTest_Success(List<FlavorType> flavorTypes, List<Integer> foodIndex, List<String> flavorRequset, List<String> matchTitles) throws Exception {
        // given
        List<Flavor> flavors = flavorTypes.stream().map(this::findFlavor)
                .collect(Collectors.toList());

        for (int index : foodIndex) {
            Food food = this.foods.get(index);
            for (Flavor flavor : flavors) {
                foodFlavorRepository.save(new FoodFlavor(food, flavor));
            }
        }

        FoodPageSearchRequest foodPageSearchRequest = FoodPageSearchRequest
                .builder()
                .minPrice(null)
                .maxPrice(null)
                .sort("id")
                .order("desc")
                .categoryName("category")
                .offset(0L)
                .pageSize(5)
                .tags(new ArrayList<>())
                .flavors(flavorRequset)
                .firstSearchTime(LocalDateTime.now())
                .build();

        em.flush();
        em.clear();

        // when
        FoodPageResponse foodPageResponse = foodService.searchFoodsPage(foodPageSearchRequest);

        // then
        assertThat(foodPageResponse.getFoods())
                .isNotNull()
                .extracting("foodTitle")
                .containsExactlyInAnyOrderElementsOf(matchTitles);
    }

    static Stream<Arguments> foodSearchWithFlavorsTest_Success() {
        return Stream.of(
                Arguments.of(List.of(FlavorType.BITTER, FlavorType.COOL_DETAIL), List.of(1, 2, 3, 4, 5, 6), List.of("쓴맛", "시원한"),
                        List.of("title_6", "title_5", "title_4", "title_3", "title_2"))
        );
    }
}
