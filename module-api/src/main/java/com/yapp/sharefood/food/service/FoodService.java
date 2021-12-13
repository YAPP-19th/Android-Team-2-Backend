package com.yapp.sharefood.food.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.common.exception.ForbiddenException;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.common.utils.LocalDateTimePeriodUtils;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.repository.FlavorRepository;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodReportStatus;
import com.yapp.sharefood.food.domain.FoodTag;
import com.yapp.sharefood.food.domain.TagWrapper;
import com.yapp.sharefood.food.dto.*;
import com.yapp.sharefood.food.dto.request.FoodCreationRequest;
import com.yapp.sharefood.food.dto.request.FoodPageSearchRequest;
import com.yapp.sharefood.food.dto.request.FoodTopRankRequest;
import com.yapp.sharefood.food.dto.request.RecommendationFoodRequest;
import com.yapp.sharefood.food.dto.response.FoodDetailResponse;
import com.yapp.sharefood.food.dto.response.FoodPageResponse;
import com.yapp.sharefood.food.dto.response.RecommendationFoodResponse;
import com.yapp.sharefood.food.dto.response.TopRankFoodResponse;
import com.yapp.sharefood.food.exception.FoodBanndedException;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.food.repository.FoodTagRepository;
import com.yapp.sharefood.like.projection.TopLikeProjection;
import com.yapp.sharefood.like.repository.LikeRepository;
import com.yapp.sharefood.tag.domain.Tag;
import com.yapp.sharefood.tag.repository.TagRepository;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.yapp.sharefood.food.dto.FoodPageDto.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {

    private static final int MIN_PAGE_OFFSET = 0;

    private final FoodRepository foodRepository;
    private final FoodTagRepository foodTagRepository;
    private final TagRepository tagRepository;

    private final CategoryRepository categoryRepository;
    private final LikeRepository likeRepository;
    private final FlavorRepository flavorRepository;

    private final FoodImageService foodImageService;

    @Transactional
    public Long saveFood(User user, FoodCreationRequest foodCreationRequest, List<TagWrapper> wrapperTags) {
        Category findCategory = categoryRepository.findByName(foodCreationRequest.getCategoryName())
                .orElseThrow(CategoryNotFoundException::new);
        List<Flavor> flavors = flavorRepository.findByFlavorTypeIsIn(
                foodCreationRequest.getFlavors().stream()
                        .map(flavorDto -> FlavorType.of(flavorDto.getFlavorName()))
                        .collect(Collectors.toList()));

        Food food = Food.builder()
                .foodTitle(foodCreationRequest.getTitle())
                .foodStatus(foodCreationRequest.getFoodStatus())
                .price(foodCreationRequest.getPrice())
                .reviewMsg(foodCreationRequest.getReviewMsg())
                .writer(user)
                .category(findCategory)
                .build();

        food.assignWrapperTags(wrapperTags);
        food.assignFlavors(flavors);
        Food saveFood = foodRepository.save(food);

        user.addPointByRegisterFood(food);
        user.addPointByOpenFood(food);
        user.upgrade();

        return saveFood.getId();
    }


    public FoodDetailResponse findFoodDetailById(User user, Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(FoodNotFoundException::new);

        if(food.getReportStatus() != FoodReportStatus.NORMAL) throw new FoodBanndedException();

        return FoodDetailResponse.builder()
                .id(food.getId())
                .foodTitle(food.getFoodTitle())
                .writerName(food.getWriterNickname())
                .reviewDetail(food.getReviewMsg())
                .price(food.getPrice())
                .numberOfLike(food.getLikeNumber())
                .isMeLike(food.isMeLike(user))
                .isMeBookmark(food.isMeBookMark(user))
                .foodImages(FoodImageDto.toList(food.getImages().getImages()))
                .foodTags(findFoodTagsByFoodTag(food.getFoodTags().getFoodTags()))
                .build();
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

    private List<FoodTagDto> findFoodTagsByFoodTag(List<FoodTag> foodTags) {
        List<Long> tagIds = foodTags.stream()
                .map(FoodTag::getId)
                .collect(Collectors.toList());
        return foodTagRepository.findFoodtagsWithTag(tagIds)
                .stream().map(foodTag -> FoodTagDto.of(foodTag.getTag().getId(), foodTag.getTag().getName(), foodTag.getIngredientType()))
                .collect(Collectors.toList());
    }

    public TopRankFoodResponse findTopRankFoods(FoodTopRankRequest foodTopRankRequest, User user) {
        LocalDateTime before = LocalDateTimePeriodUtils.getBeforePeriod(foodTopRankRequest.getRankDatePeriod());
        LocalDateTime now = LocalDateTimePeriodUtils.now();

        List<FoodPageDto> foodPageDtos = getTopRankPageData(foodTopRankRequest.getTop(), foodTopRankRequest.getCategoryName(), before, now, user);
        return TopRankFoodResponse.of(foodPageDtos);
    }

    private List<FoodPageDto> getTopRankPageData(int rank, String categoryName, LocalDateTime before, LocalDateTime now, User user) {
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

    public RecommendationFoodResponse findFoodRecommendation(RecommendationFoodRequest recommendationFoodRequest, User user) {

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

        List<Food> pageFoods = findFoodPageBySearch(foodPageSearch);

        if (pageFoods.size() < foodPageSearchRequest.getPageSize()) {
            return FoodPageResponse.ofLastPage(pageFoods, foodPageSearchRequest.getPageSize(), user);
        }

        return FoodPageResponse.of(pageFoods, foodPageSearchRequest.getPageSize(), foodPageSearch.getOffset(), user);
    }

    private List<Food> findFoodPageBySearch(FoodPageSearch foodPageSearch) {
        if (!foodPageSearch.getTags().isEmpty() && !foodPageSearch.getFlavors().isEmpty()) {
            throw new InvalidOperationException("flavor와 tag는 중복으로 filter 할 수 없습니다.");
        }

        if (!foodPageSearch.getTags().isEmpty()) {
            return foodRepository.findFoodFilterWithTag(foodPageSearch);
        } else if (!foodPageSearch.getFlavors().isEmpty()) {
            return foodRepository.findFoodFilterWithFlavor(foodPageSearch);
        }

        return foodRepository.findFoodNormalSearch(foodPageSearch);
    }
}
