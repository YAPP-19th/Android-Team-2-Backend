package com.yapp.sharefood.food.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.common.exception.BadRequestException;
import com.yapp.sharefood.common.exception.ForbiddenException;
import com.yapp.sharefood.common.order.SortType;
import com.yapp.sharefood.common.utils.LocalDateTimePeriodUtils;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.repository.FlavorRepository;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodReportStatus;
import com.yapp.sharefood.food.domain.TagWrapper;
import com.yapp.sharefood.food.dto.*;
import com.yapp.sharefood.food.dto.request.*;
import com.yapp.sharefood.food.dto.response.FoodDetailResponse;
import com.yapp.sharefood.food.dto.response.FoodPageResponse;
import com.yapp.sharefood.food.dto.response.RecommendationFoodResponse;
import com.yapp.sharefood.food.dto.response.TopRankFoodResponse;
import com.yapp.sharefood.food.exception.FoodBanndedException;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.like.projection.TopLikeProjection;
import com.yapp.sharefood.like.repository.LikeRepository;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.tag.domain.Tag;
import com.yapp.sharefood.tag.repository.TagRepository;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yapp.sharefood.food.dto.FoodPageDto.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {

    private static final int MIN_PAGE_OFFSET = 0;
    private final Map<String, FoodPageReadStrategy> foodPageReadStrategyMap;

    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final TagRepository tagRepository;

    private final CategoryRepository categoryRepository;
    private final LikeRepository likeRepository;
    private final FlavorRepository flavorRepository;

    private final FoodImageService foodImageService;

    @Transactional
    public Long saveFood(User user, FoodCreationRequest foodCreationRequest, List<TagWrapper> wrapperTags) {
        User findUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);

        Category findCategory = categoryRepository.findByName(foodCreationRequest.getCategoryName())
                .orElseThrow(CategoryNotFoundException::new);
        List<Flavor> flavors = flavorRepository.findByFlavorTypeIsIn(
                foodCreationRequest.getFlavors().stream().map(FlavorType::of)
                        .collect(Collectors.toList()));

        Food food = Food.builder()
                .foodTitle(foodCreationRequest.getTitle())
                .foodStatus(foodCreationRequest.getFoodStatus())
                .price(foodCreationRequest.getPrice())
                .reviewMsg(foodCreationRequest.getReviewMsg())
                .writer(findUser)
                .category(findCategory)
                .build();

        food.assignWrapperTags(wrapperTags);
        food.assignFlavors(flavors);
        Food saveFood = foodRepository.save(food);

        findUser.addPointByRegisterFood(food);
        findUser.addPointByOpenFood(food);
        findUser.upgrade();

        return saveFood.getId();
    }

    @Transactional
    public FoodDetailResponse updateFood(User user, Long foodId, List<TagWrapper> wrapperTags, FoodUpdateRequest foodUpdateRequest) {
        Food findFood = foodRepository.findByIdWithUser(foodId, user)
                .orElseThrow(FoodNotFoundException::new);
        Category category = categoryRepository.findByName(foodUpdateRequest.getCategoryName())
                .orElseThrow(CategoryNotFoundException::new);

        List<Flavor> flavors = flavorRepository.findByFlavorTypeIsIn(
                foodUpdateRequest.getFlavors().stream()
                        .map(flavorDto -> FlavorType.of(flavorDto.getFlavorName()))
                        .collect(Collectors.toList()));

        findFood.updateAllElements(foodUpdateRequest.getTitle(),
                foodUpdateRequest.getReviewMsg(),
                foodUpdateRequest.getPrice(),
                foodUpdateRequest.getFoodStatus(),
                category);

        findFood.getFoodFlavors().updateFlavors(flavors, findFood); // update flavors
        findFood.getFoodTags().updateTags(wrapperTags, findFood); // update tags

        return FoodDetailResponse.toFoodDetailDto(user, findFood);
    }


    public FoodDetailResponse findFoodDetailById(User user, Long foodId) {
        Food food = foodRepository.findFoodWithWriterAndCategoryById(foodId).orElseThrow(FoodNotFoundException::new);

        if (food.getReportStatus() != FoodReportStatus.NORMAL)
            throw new FoodBanndedException();

        return FoodDetailResponse.toFoodDetailDto(user, food);
    }

    @Transactional
    public void deleteFood(Long id, User authUser) {
        Food findFood = foodRepository.findById(id)
                .orElseThrow(FoodNotFoundException::new);

        validateAuthUser(findFood, authUser);
        foodImageService.deleteImages(findFood.getImages().getImages());
        foodRepository.delete(findFood);
    }

    private void validateAuthUser(Food food, User user) {
        if (!food.isAuth(user)) {
            throw new ForbiddenException();
        }
    }

    public TopRankFoodResponse findTopRankFoods(FoodTopRankRequest foodTopRankRequest, User user) {
        LocalDateTime before = LocalDateTimePeriodUtils.getBeforePeriod(foodTopRankRequest.getRankDatePeriod());
        LocalDateTime now = LocalDateTimePeriodUtils.now();

        List<FoodPageDto> foodPageDtos = getTopRankPageData(foodTopRankRequest.getTop(), foodTopRankRequest.getCategoryName(), before, now, user);
        return TopRankFoodResponse.of(foodPageDtos);
    }

    private List<FoodPageDto> getTopRankPageData(int rank, String categoryName, LocalDateTime
            before, LocalDateTime now, User user) {
        List<Category> categoryWithChildrenByName = findCategoryWithChildrenByName(categoryName);
        List<TopLikeProjection> topFoodIdsByCount =
                likeRepository.findTopFoodIdsByCount(rank, categoryWithChildrenByName, before, now);

        List<Long> foodIds = topFoodIdsByCount.stream()
                .map(TopLikeProjection::getFoodId)
                .collect(Collectors.toList());

        return toList(foodRepository.findFoodWithCategoryByIds(foodIds), user)
                .stream().sorted(Comparator.comparing(foodPageDto -> -foodPageDto.getNumberOfLikes()))
                .collect(Collectors.toList());
    }

    private List<Category> findCategoryWithChildrenByName(String categoryName) {
        Category findCategory = categoryRepository.findByName(categoryName)
                .orElseThrow(CategoryNotFoundException::new);
        List<Category> allCategories = new ArrayList<>();
        allCategories.add(findCategory);
        allCategories.addAll(findCategory.getChildCategories().getChildCategories());

        return allCategories;
    }

    public RecommendationFoodResponse findFoodRecommendation(RecommendationFoodRequest
                                                                     recommendationFoodRequest, User user) {

        LocalDateTime before = LocalDateTimePeriodUtils.getBeforePeriod(recommendationFoodRequest.getRankDatePeriod());
        LocalDateTime now = LocalDateTimePeriodUtils.now();

        List<Flavor> userSettingFlavors = flavorRepository.findByUser(user);

        if (userSettingFlavors.isEmpty()) {
            List<FoodPageDto> topRankPageData = getTopRankPageData(recommendationFoodRequest.getTop(), recommendationFoodRequest.getCategoryName(), before, now, user);
            return new RecommendationFoodResponse(topRankPageData);
        }

        List<Category> categories = findCategoryWithChildrenByName(recommendationFoodRequest.getCategoryName());
        FoodRecommendSearch foodRecommendSearch = new FoodRecommendSearch(recommendationFoodRequest.getTop(), before, now, userSettingFlavors, categories);
        return new RecommendationFoodResponse(toList(foodRepository.findRecommendFoods(foodRecommendSearch), user));
    }

    public FoodPageResponse searchFoodsPage(FoodPageSearchRequest foodPageSearchRequest, User user) {
        if (foodPageSearchRequest.getOffset() < MIN_PAGE_OFFSET) {
            return FoodPageResponse.ofLastPage(List.of(), foodPageSearchRequest.getPageSize(), user);
        }

        Category category = categoryRepository.findByName(foodPageSearchRequest.getCategoryName())
                .orElseThrow(CategoryNotFoundException::new);
        List<Tag> tags = tagRepository.findByNameIn(foodPageSearchRequest.getTags());
        List<Flavor> flavors = flavorRepository.findByFlavorTypeIsIn(FlavorType.toList(foodPageSearchRequest.getFlavors()));

        if ((foodPageSearchRequest.getTags().size() > 0 && tags.size() == 0)
                || (foodPageSearchRequest.getFlavors().size() > 0 && flavors.size() == 0)) {
            return FoodPageResponse.ofLastPage(new ArrayList<>(), foodPageSearchRequest.getPageSize(), user);
        }

        FoodPageSearch foodPageSearch = FoodPageSearch.builder()
                .minPrice(foodPageSearchRequest.getMinPrice())
                .maxPrice(foodPageSearchRequest.getMaxPrice())
                .size(foodPageSearchRequest.getPageSize())
                .sort(foodPageSearchRequest.getSort())
                .order(foodPageSearchRequest.getOrder())
                .offset(foodPageSearchRequest.getOffset())
                .category(category)
                .tags(tags)
                .flavors(flavors)
                .searchTime(foodPageSearchRequest.getFirstSearchTime())
                .build();

        List<Food> pageFoods = foodPageReadStrategyMap.get(FoodPageReadType.of(foodPageSearch).getKey()).findFoodPageBySearch(foodPageSearch);

        if (pageFoods.size() < foodPageSearchRequest.getPageSize()) {
            return FoodPageResponse.ofLastPage(pageFoods, foodPageSearchRequest.getPageSize(), user);
        }

        return FoodPageResponse.of(pageFoods, foodPageSearchRequest.getPageSize(), foodPageSearch.getOffset(), user);
    }

    public FoodPageResponse findOnlyMineFoods(User user, FoodMinePageSearchRequest foodMinePageSearchRequest) {
        List<Flavor> flavors = flavorRepository.findByFlavorTypeIsIn(FlavorType.toList(foodMinePageSearchRequest.getFlavors()));
        if (foodMinePageSearchRequest.getFlavors().size() > 0 && flavors.isEmpty()) {
            return FoodPageResponse.ofLastPage(new ArrayList<>(), foodMinePageSearchRequest.getPageSize(), user);
        }
        List<Category> categoryWithChildrenByName = findCategoryWithChildrenByName(foodMinePageSearchRequest.getCategoryName());
        List<Food> pageFoods = findMineFoods(user, flavors, categoryWithChildrenByName, foodMinePageSearchRequest);

        if (pageFoods.size() < foodMinePageSearchRequest.getPageSize()) {
            return FoodPageResponse.ofLastPage(pageFoods, foodMinePageSearchRequest.getPageSize(), user);
        }

        return FoodPageResponse.of(pageFoods, foodMinePageSearchRequest.getPageSize(), foodMinePageSearchRequest.getOffset(), user);
    }


    private List<Food> findMineFoods(User user, List<Flavor> flavors, List<Category> categories,
                                     FoodMinePageSearchRequest foodMinePageSearchRequest) {
        FoodMinePageSearch foodMinePageSearch = FoodMinePageSearch.builder()
                .minPrice(foodMinePageSearchRequest.getMinPrice())
                .maxPrice(foodMinePageSearchRequest.getMaxPrice())
                .flavors(flavors)
                .sort(SortType.of(foodMinePageSearchRequest.getSort()))
                .order(OrderType.of(foodMinePageSearchRequest.getOrder()))
                .categories(categories)
                .offset(foodMinePageSearchRequest.getOffset())
                .size(foodMinePageSearchRequest.getPageSize())
                .status(foodMinePageSearchRequest.getStatus())
                .searchTime(foodMinePageSearchRequest.getFirstSearchTime())
                .isFlavorSearchExist(!foodMinePageSearchRequest.getFlavors().isEmpty())
                .build();

        if (foodMinePageSearchRequest.getMineFoodType() == MineFoodType.MYFOOD) {
            return foodRepository.findMineFoodSearch(user, foodMinePageSearch);
        } else if (foodMinePageSearchRequest.getMineFoodType() == MineFoodType.BOOKMARK) {
            return foodRepository.findMineBookMarkFoodSearch(user, foodMinePageSearch);
        }

        throw new BadRequestException();
    }
}
