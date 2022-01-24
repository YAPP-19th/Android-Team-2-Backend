package com.yapp.sharefood.food.service;

import com.yapp.sharefood.bookmark.domain.Bookmark;
import com.yapp.sharefood.bookmark.repository.BookmarkRepository;
import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.common.order.SortType;
import com.yapp.sharefood.common.service.IntegrationService;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.repository.FlavorRepository;
import com.yapp.sharefood.food.domain.*;
import com.yapp.sharefood.food.dto.FoodPageDto;
import com.yapp.sharefood.food.dto.MineFoodType;
import com.yapp.sharefood.food.dto.OrderType;
import com.yapp.sharefood.food.dto.request.FoodMinePageSearchRequest;
import com.yapp.sharefood.food.dto.request.FoodPageSearchRequest;
import com.yapp.sharefood.food.dto.response.FoodPageResponse;
import com.yapp.sharefood.food.repository.FoodFlavorRepository;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.food.repository.FoodTagRepository;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class FoodFindPageTest extends IntegrationService {

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
    @Autowired
    FoodTagRepository foodTagRepository;
    @Autowired
    BookmarkRepository bookmarkRepository;

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
            if (i == 9) food.addReport(FoodReportType.POSTING_OBSCENE_CONTENT.getMessage());
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
        User user = saveTestUser("nickname_for_test", "name_for_inneer_test", "oauthId_test");
        FoodPageSearchRequest foodPageSearchRequest = FoodPageSearchRequest
                .builder()
                .sort("id")
                .order("desc")
                .categoryName("category")
                .offset(0L)
                .pageSize(5)
                .tags(new ArrayList<>())
                .flavors(new ArrayList<>())
                .firstSearchTime(LocalDateTime.now().plusDays(3))
                .build();

        // when
        FoodPageResponse foodPageResponse = foodService.searchFoodsPage(foodPageSearchRequest, user);

        // then
        List<Food> expectList = foods.stream()
                .filter(food -> food.getReportStatus() == FoodReportStatus.NORMAL)
                .sorted(Comparator.comparing(Food::getId).reversed())
                .collect(Collectors.toList());

        assertEquals(5, foodPageResponse.getPageSize());
        assertEquals(0L, foodPageResponse.getOffset());
        assertThat(foodPageResponse.getFoods())
                .hasSize(5);
        Long firstFoodId = expectList.get(0).getId();
        assertThat(firstFoodId).isNotNull();
        FoodPageDto firstSearchFood = foodPageResponse.getFoods().get(0);
        assertEquals(firstFoodId, firstSearchFood.getId());
    }

    @MethodSource
    @ParameterizedTest(name = "price로 filter를 정한 경우 id로 order - 성공")
    void foodPageSearchByPriceTest_Success(Integer minPrice, Integer maxPrice, long expectedPageOffset, int expectedPageSize, List<String> extractTitles) throws Exception {
        // given
        User user = saveTestUser("nickname_for_test", "name_for_inneer_test", "oauthId_test");
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
                .firstSearchTime(LocalDateTime.now().plusDays(3))
                .build();

        // when
        FoodPageResponse foodPageResponse = foodService.searchFoodsPage(foodPageSearchRequest, user);

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
                Arguments.of(null, null, 0L, 5, List.of("title_8", "title_7", "title_6", "title_5", "title_4")),
                Arguments.of(null, 8, 0L, 5, List.of("title_8", "title_7", "title_6", "title_5", "title_4")),
                Arguments.of(7, null, -1L, 5, List.of("title_8", "title_7")),
                Arguments.of(2, 4, -1L, 5, List.of("title_4", "title_3", "title_2"))
        );
    }

    @Test
    @DisplayName("Tag와 Flavor 로 동시에 조회할 경우 에러 발생")
    void foodFlavorTagSameRequest_Exception() throws Exception {
        // given
        User user = saveTestUser("nickname_for_test", "name_for_inneer_test", "oauthId_test");
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
        assertThrows(InvalidOperationException.class, () -> foodService.searchFoodsPage(foodPageSearchRequest, user));
    }

    @MethodSource
    @ParameterizedTest(name = "food flavor 로 조회한 케이스 테스트")
    void foodSearchWithFlavorsTest_Success(List<FlavorType> flavorTypes, List<Integer> foodIndex, List<String> flavorRequest, List<String> matchTitles) throws Exception {
        // given
        User user = saveTestUser("nickname_for_test", "name_for_inneer_test", "oauthId_test");
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
                .flavors(flavorRequest)
                .firstSearchTime(LocalDateTime.now())
                .build();

        em.flush();
        em.clear();

        // when
        FoodPageResponse foodPageResponse = foodService.searchFoodsPage(foodPageSearchRequest, user);

        // then
        assertThat(foodPageResponse.getFoods())
                .isNotNull()
                .extracting("foodTitle")
                .containsExactlyInAnyOrderElementsOf(matchTitles);
    }

    static Stream<Arguments> foodSearchWithFlavorsTest_Success() {
        return Stream.of(
                Arguments.of(List.of(FlavorType.BITTER, FlavorType.COOL_DETAIL), List.of(1, 2, 3, 4, 5, 6), List.of(FlavorType.BITTER.getFlavorName(), FlavorType.COOL_DETAIL.getFlavorName()),
                        List.of("title_6", "title_5", "title_4", "title_3", "title_2")),
                Arguments.of(List.of(FlavorType.BITTER, FlavorType.COOL_DETAIL), List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), List.of(FlavorType.BITTER.getFlavorName(), FlavorType.COOL_DETAIL.getFlavorName()),
                        List.of("title_8", "title_7", "title_6", "title_5", "title_4")),
                Arguments.of(List.of(FlavorType.BITTER, FlavorType.COOL_DETAIL), new ArrayList<>(), List.of(FlavorType.BITTER.getFlavorName(), FlavorType.COOL_DETAIL.getFlavorName()),
                        new ArrayList<>())
        );
    }

    @MethodSource
    @ParameterizedTest(name = "food tag로 조회한 케이스 테스트")
    void foodSearchFromTag_Success(List<String> tagNames, List<Integer> foodIndexs, List<String> matchTitles) throws Exception {
        // given
        User user = saveTestUser("nickname_for_test", "name_for_inneer_test", "oauthId_test");
        List<TagWrapper> findTags = tagRepository.findByNameIn(tagNames)
                .stream().map(tag -> new TagWrapper(tag, FoodIngredientType.ADD))
                .collect(Collectors.toList());

        for (int foodIndex : foodIndexs) {
            Food findFood = this.foods.get(foodIndex);
            findFood.assignWrapperTags(findTags);
            foodTagRepository.saveAll(findFood.getFoodTags().getFoodTags());
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
                .tags(tagNames)
                .flavors(new ArrayList<>())
                .firstSearchTime(LocalDateTime.now())
                .build();

        em.flush();
        em.clear();

        // when
        FoodPageResponse foodPageResponse = foodService.searchFoodsPage(foodPageSearchRequest, user);

        // then
        assertThat(foodPageResponse.getFoods())
                .isNotNull()
                .extracting("foodTitle")
                .containsExactlyInAnyOrderElementsOf(matchTitles);
    }

    static Stream<Arguments> foodSearchFromTag_Success() {
        return Stream.of(
                Arguments.of(List.of("카푸치노", "시럽", "크림"), List.of(1, 2, 3, 4, 5, 6), List.of("title_6", "title_5", "title_4", "title_3", "title_2")),
                Arguments.of(List.of("카푸치노", "시럽", "크림"), List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), List.of("title_8", "title_7", "title_6", "title_5", "title_4")),
                Arguments.of(List.of("카푸치노", "시럽", "크림"), new ArrayList<>(), new ArrayList<>())
        );
    }

    @MethodSource
    @ParameterizedTest(name = "나의 레시피 조회 기능 테스트")
    void findMineFoodTest_Success(List<FlavorType> flavorTypes, List<Integer> flavorAssignFoodIndex,
                                  List<String> realDataTitles) throws Exception {
        // given
        User ownerUser = this.ownerUser;
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(3L);

        List<Flavor> flavors = flavorTypes.stream()
                .map(this::findFlavor)
                .collect(Collectors.toList());
        List<String> flavorNames = new ArrayList<>();

        for (int index : flavorAssignFoodIndex) {
            Food food = this.foods.get(index);
            for (Flavor flavor : flavors) {
                foodFlavorRepository.save(new FoodFlavor(food, flavor));
            }
        }
        for (Flavor flavor : flavors) {
            flavorNames.add(flavor.getFlavorType().getFlavorName());
        }

        FoodMinePageSearchRequest foodMinePageSearch = FoodMinePageSearchRequest.builder()
                .flavors(flavorNames)
                .sort(SortType.ID.getValue())
                .order(OrderType.DESC.getOrder())
                .categoryName(category.getName())
                .offset(0L)
                .pageSize(3)
                .mineFoodType(MineFoodType.MYFOOD)
                .firstSearchTime(localDateTime)
                .build();

        // when
        FoodPageResponse onlyMineFoods = foodService.findOnlyMineFoods(ownerUser, foodMinePageSearch);

        // then
        assertThat(onlyMineFoods.getFoods())
                .hasSize(realDataTitles.size())
                .extracting("foodTitle")
                .containsExactlyInAnyOrderElementsOf(realDataTitles);
    }

    static Stream<Arguments> findMineFoodTest_Success() {
        return Stream.of(
                Arguments.of(List.of(), List.of(), List.of("title_8", "title_7", "title_6")),
                Arguments.of(List.of(FlavorType.BITTER, FlavorType.SPICY), List.of(7, 6, 5), List.of("title_7", "title_6", "title_5"))
        );
    }


    @MethodSource
    @ParameterizedTest(name = "Bookmark한 레시피 조회 기능 테스트")
    void findMineBookMarkFoodTest_Success(List<FlavorType> flavorTypes, List<Integer> flavorAssignFoodIndex,
                                          List<String> realDataTitles) throws Exception {
        // given
        List<Food> otherUserFood = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Food food = Food.builder()
                    .foodTitle("other_title_" + i)
                    .foodStatus(FoodStatus.SHARED)
                    .category(this.category)
                    .price(i)
                    .reviewMsg("review")
                    .writer(this.otherUser)
                    .build();
            if (i == 9) food.addReport(FoodReportType.POSTING_OBSCENE_CONTENT.getMessage());
            otherUserFood.add(food);
        }
        foodRepository.saveAll(otherUserFood);

        User ownerUser = this.ownerUser;
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(3L);

        List<Flavor> flavors = flavorTypes.stream().map(this::findFlavor)
                .collect(Collectors.toList());
        List<String> flavorNames = new ArrayList<>();

        for (int index : flavorAssignFoodIndex) {
            Food food = otherUserFood.get(index);
            for (Flavor flavor : flavors) {
                foodFlavorRepository.save(new FoodFlavor(food, flavor));
            }

            Bookmark bookmark = Bookmark.of(this.ownerUser);
            food.assignBookmark(bookmark);
            bookmarkRepository.save(bookmark);
        }
        for (Flavor flavor : flavors) {
            flavorNames.add(flavor.getFlavorType().getFlavorName());
        }

        em.flush();
        em.clear();

        FoodMinePageSearchRequest foodMinePageSearch = FoodMinePageSearchRequest.builder()
                .flavors(flavorNames)
                .sort(SortType.ID.getValue())
                .order(OrderType.DESC.getOrder())
                .categoryName(category.getName())
                .offset(0L)
                .pageSize(3)
                .mineFoodType(MineFoodType.BOOKMARK)
                .firstSearchTime(localDateTime)
                .build();

        // when
        FoodPageResponse onlyMineFoods = foodService.findOnlyMineFoods(ownerUser, foodMinePageSearch);

        // then
        assertThat(onlyMineFoods.getFoods())
                .hasSize(realDataTitles.size())
                .extracting("foodTitle")
                .containsExactlyInAnyOrderElementsOf(realDataTitles);
    }

    static Stream<Arguments> findMineBookMarkFoodTest_Success() {
        return Stream.of(
                Arguments.of(List.of(), List.of(), List.of()),
                Arguments.of(List.of(), List.of(7, 6, 5), List.of("other_title_7", "other_title_6", "other_title_5")),
                Arguments.of(List.of(FlavorType.BITTER, FlavorType.SPICY), List.of(7, 6, 5), List.of("other_title_7", "other_title_6", "other_title_5"))
        );
    }
}
