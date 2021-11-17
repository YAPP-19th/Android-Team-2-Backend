package com.yapp.sharefood.food.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.dto.CategoryDto;
import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.external.s3.AwsS3Uploader;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodTag;
import com.yapp.sharefood.food.domain.TagWrapper;
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
import com.yapp.sharefood.image.domain.Image;
import com.yapp.sharefood.image.repository.ImageRepository;
import com.yapp.sharefood.like.projection.TopLikeProjection;
import com.yapp.sharefood.like.repository.LikeRepository;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private static final String FOOD_FILE_PATH = "food";

    private final FoodRepository foodRepository;
    private final FoodTagRepository foodTagRepository;
    private final CategoryRepository categoryRepository;
    private final LikeRepository likeRepository;
    private final ImageRepository imageRepository;
    private final AwsS3Uploader awsS3Uploader;

    @Transactional
    public Long saveFood(User user, FoodCreationRequest foodCreationRequest, List<TagWrapper> wrapperTags) {
        Category findCategory = categoryRepository.findByName(foodCreationRequest.getCategoryName())
                .orElseThrow(CategoryNotFoundException::new);

        Food food = Food.builder()
                .foodTitle(foodCreationRequest.getTitle())
                .foodStatus(foodCreationRequest.getFoodStatus())
                .price(foodCreationRequest.getPrice())
                .reviewMsg(foodCreationRequest.getReviewMsg())
                .writer(user)
                .category(findCategory)
                .build();

        food.getFoodTags().addAllTags(wrapperTags, food);
        Food saveFood = foodRepository.save(food);
        uploadImage(saveFood, foodCreationRequest.getImages());

        return saveFood.getId();
    }

    private void uploadImage(Food food, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) return;

        for (MultipartFile file : images) {
            String uploadFileName = awsS3Uploader.upload(FOOD_FILE_PATH, file);
            Image image = Image.builder()
                    .realFilename(file.getOriginalFilename())
                    .storeFilename(uploadFileName)
                    .build();
            image.assignFood(food);
            imageRepository.save(image);
        }
    }

    public FoodDetailResponse findFoodById(Long id) {
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


    public TopRankFoodResponse findTopRankFoods(FoodTopRankRequest foodTopRankRequest, String categoryName, LocalDateTime before, LocalDateTime now) {
        List<Category> categoryWithChildrenByName = findCategoryWithChildrenByName(categoryName);
        List<TopLikeProjection> topFoodIdsByCount =
                likeRepository.findTopFoodIdsByCount(foodTopRankRequest.getTop(), categoryWithChildrenByName, before, now);

        Map<Long, Long> foodIdKeylikeCountMap = topFoodIdsByCount.stream()
                .collect(toMap(TopLikeProjection::getFoodId, TopLikeProjection::getCount));
        List<Long> foodIds = topFoodIdsByCount.stream()
                .map(TopLikeProjection::getFoodId)
                .collect(Collectors.toList());

        List<FoodPageDto> foodPageDtos = toList(foodRepository.findFoodWithCategoryByIds(foodIds), foodIdKeylikeCountMap)
                .stream().sorted(Comparator.comparing(foodPageDto -> -foodPageDto.getNumberOfLikes()))
                .collect(Collectors.toList());
        return TopRankFoodResponse.of(foodPageDtos);
    }

    private List<Category> findCategoryWithChildrenByName(String categoryName) {
        Category findCategory = categoryRepository.findByName(categoryName)
                .orElseThrow(CategoryNotFoundException::new);
        List<Category> allCategories = new ArrayList<>();
        allCategories.add(findCategory);
        allCategories.addAll(findCategory.getChildCategories().getChildCategories());

        return allCategories;
    }
}
