package com.yapp.sharefood.food.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.dto.CategoryDto;
import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodTag;
import com.yapp.sharefood.food.dto.FoodImageDto;
import com.yapp.sharefood.food.dto.FoodPageDto;
import com.yapp.sharefood.food.dto.FoodTagDto;
import com.yapp.sharefood.food.dto.request.FoodCreationRequest;
import com.yapp.sharefood.food.dto.request.FoodTopRankRequest;
import com.yapp.sharefood.food.dto.response.FoodDetailResponse;
import com.yapp.sharefood.food.dto.response.FoodPageResponse;
import com.yapp.sharefood.food.dto.response.TopRankFoodResponse;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.food.repository.FoodTagRepository;
import com.yapp.sharefood.like.projection.TopLikeProjection;
import com.yapp.sharefood.like.repository.LikeRepository;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final CategoryRepository categoryRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public Long saveFood(User user, FoodCreationRequest foodCreationRequest, String categoryName) {
        Category findCategory = categoryRepository.findByName(categoryName)
                .orElseThrow(CategoryNotFoundException::new);

        Food food = Food.builder()
                .foodTitle(foodCreationRequest.getTitle())
                .foodStatus(foodCreationRequest.getFoodStatus())
                .price(foodCreationRequest.getPrice())
                .reviewMsg(foodCreationRequest.getReviewMsg())
                .writer(user)
                .category(findCategory)
                .build();

        Food save = foodRepository.save(food);

        return save.getId();
    }

    public FoodDetailResponse findFoodById(Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(FoodNotFoundException::new);
        List<FoodImageDto> foodImageDtos = food.getImages().getImages()
                .stream()
                .map(img -> new FoodImageDto(img.getId(), img.getStoreFilename(), img.getRealFilename()))
                .collect(Collectors.toList());
        List<FoodTagDto> tags = findFoodTagsByFoodTag(food.getFoodTags().getFoodTags());

        return FoodDetailResponse
                .builder()
                .title(food.getFoodTitle())
                .writerName(food.getWriterNickname())
                .reviewDetail(food.getReviewMsg())
                .price(food.getPrice())
                .foodImages(foodImageDtos)
                .foodTags(tags)
                .build();
    }

    private List<FoodTagDto> findFoodTagsByFoodTag(List<FoodTag> foodTags) {
        List<Long> tagIds = foodTags.stream()
                .map(FoodTag::getId)
                .collect(Collectors.toList());
        return foodTagRepository.findFoodtagsWithTag(tagIds)
                .stream().map(foodTag -> FoodTagDto.of(foodTag.getTag().getId(), foodTag.getTag().getName(), foodTag.getIngredientType()))
                .collect(Collectors.toList());
    }

    public FoodPageResponse findAllFoods(CategoryDto categoryDto, Pageable pageable) {
        Category category = categoryRepository.findByName(categoryDto.getCategoryName())
                .orElseThrow(CategoryNotFoundException::new);
        Slice<FoodPageDto> findFoods = foodRepository.findByCategory(category, pageable)
                .map(food -> FoodPageDto.toFoodPageDto(food, 0L));

        return new FoodPageResponse(findFoods);
    }


    public TopRankFoodResponse findTopRankFoods(FoodTopRankRequest foodTopRankRequest, LocalDateTime before, LocalDateTime now) {
        List<TopLikeProjection> topFoodIdsByCount =
                likeRepository.findTopFoodIdsByCount(foodTopRankRequest.getTop(), before, now);

        Map<Long, Long> foodIdKeylikeCountMap = topFoodIdsByCount.stream()
                .collect(toMap(TopLikeProjection::getFoodId, TopLikeProjection::getCount));
        List<Long> foodIds = topFoodIdsByCount.stream()
                .map(TopLikeProjection::getFoodId)
                .collect(Collectors.toList());

        return TopRankFoodResponse.of(toList(foodRepository.findFoodWithCategoryByIds(foodIds), foodIdKeylikeCountMap));
    }
}
