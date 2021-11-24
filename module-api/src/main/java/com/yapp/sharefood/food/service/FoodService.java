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
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.food.repository.FoodTagRepository;
import com.yapp.sharefood.like.projection.TopLikeProjection;
import com.yapp.sharefood.like.repository.LikeRepository;
import com.yapp.sharefood.tag.domain.Tag;
import com.yapp.sharefood.tag.repository.TagRepository;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.userflavor.domain.UserFlavor;
import com.yapp.sharefood.userflavor.repository.UserFlavorRepository;
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
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {

    private final FoodRepository foodRepository;
    private final FoodTagRepository foodTagRepository;
    private final TagRepository tagRepository;

    private final UserFlavorRepository userFlavorRepository;

    private final CategoryRepository categoryRepository;
    private final LikeRepository likeRepository;
    private final FlavorRepository flavorRepository;

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

        food.assignWrapperTags(wrapperTags, food);
        food.assignFlavors(flavors);
        Food saveFood = foodRepository.save(food);

        return saveFood.getId();
    }


    public FoodDetailResponse findFoodDetailById(Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(FoodNotFoundException::new);

        return FoodDetailResponse
                .builder()
                .title(food.getFoodTitle())
                .writerName(food.getWriterNickname())
                .reviewDetail(food.getReviewMsg())
                .price(food.getPrice())
                .numberOfLike(food.getLikeNumber())
                .foodImages(FoodImageDto.toList(food.getImages().getImages()))
                .foodTags(findFoodTagsByFoodTag(food.getFoodTags().getFoodTags()))
                .build();
    }

    @Transactional
    public void deleteFood(Long id, User authUser) {
        Food findFood = foodRepository.findById(id)
                .orElseThrow(FoodNotFoundException::new);

        validateAuthUser(findFood, authUser);

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

    public TopRankFoodResponse findTopRankFoods(FoodTopRankRequest foodTopRankRequest) {
        LocalDateTime before = LocalDateTimePeriodUtils.getBeforePeriod(foodTopRankRequest.getRankDatePeriod());
        LocalDateTime now = LocalDateTimePeriodUtils.now();

        List<FoodPageDto> foodPageDtos = getTopRankPageData(foodTopRankRequest.getTop(), foodTopRankRequest.getCategoryName(), before, now);
        return TopRankFoodResponse.of(foodPageDtos);
    }

    private List<FoodPageDto> getTopRankPageData(int rank, String categoryName, LocalDateTime before, LocalDateTime now) {
        List<Category> categoryWithChildrenByName = findCategoryWithChildrenByName(categoryName);
        List<TopLikeProjection> topFoodIdsByCount =
                likeRepository.findTopFoodIdsByCount(rank, categoryWithChildrenByName, before, now);

        Map<Long, Long> foodIdKeylikeCountMap = topFoodIdsByCount.stream()
                .collect(toMap(TopLikeProjection::getFoodId, TopLikeProjection::getCount));
        List<Long> foodIds = topFoodIdsByCount.stream()
                .map(TopLikeProjection::getFoodId)
                .collect(Collectors.toList());

        return toList(foodRepository.findFoodWithCategoryByIds(foodIds), foodIdKeylikeCountMap)
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

        List<Flavor> userSettingFlavors = userFlavorRepository.findByUser(user)
                .stream().map(UserFlavor::getFlavor)
                .collect(Collectors.toList());

        if (userSettingFlavors.isEmpty()) {
            List<FoodPageDto> topRankPageData = getTopRankPageData(recommendationFoodRequest.getTop(), recommendationFoodRequest.getCategoryName(), before, now);
            return new RecommendationFoodResponse(topRankPageData);
        }

        List<Category> categories = findCategoryWithChildrenByName(recommendationFoodRequest.getCategoryName());
        FoodRecommendSearch foodRecommendSearch = new FoodRecommendSearch(recommendationFoodRequest.getTop(), before, now, userSettingFlavors, categories);
        return new RecommendationFoodResponse(toList(foodRepository.findRecommendFoods(foodRecommendSearch)));
    }

    public FoodPageResponse searchFoodsPage(FoodPageSearchRequest foodPageSearchRequest) {
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
            return FoodPageResponse.ofLastPage(pageFoods, foodPageSearchRequest.getPageSize());
        }

        return FoodPageResponse.of(pageFoods, foodPageSearchRequest.getPageSize(), foodPageSearch.getOffset() + 1);
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
