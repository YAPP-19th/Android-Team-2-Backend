package com.yapp.sharefood.food.facade;

import com.yapp.sharefood.common.exception.BadRequestException;
import com.yapp.sharefood.food.domain.FoodIngredientType;
import com.yapp.sharefood.food.domain.TagWrapper;
import com.yapp.sharefood.food.dto.FoodTagDto;
import com.yapp.sharefood.food.dto.request.FoodCreationRequest;
import com.yapp.sharefood.food.dto.request.FoodUpdateRequest;
import com.yapp.sharefood.food.dto.response.FoodDetailResponse;
import com.yapp.sharefood.food.service.FoodService;
import com.yapp.sharefood.tag.exception.TagConflictException;
import com.yapp.sharefood.tag.service.TagService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FoodSaveFacade {
    private final TagService tagService;
    private final FoodService foodService;

    public Long saveFoodFacade(User user, FoodCreationRequest foodCreationRequest) {
        List<TagWrapper> wrapperTags = getSavedWrapperTags(foodCreationRequest.getTags());

        return foodService.saveFood(user, foodCreationRequest, wrapperTags);
    }

    public FoodDetailResponse updateFoodFacade(User user, Long foodId, FoodUpdateRequest foodUpdateRequest) {
        List<TagWrapper> wrapperTags = getSavedWrapperTags(foodUpdateRequest.getTags());

        return foodService.updateFood(user, foodId, wrapperTags, foodUpdateRequest);
    }

    private List<TagWrapper> getSavedWrapperTags(List<FoodTagDto> foodTags) {
        List<TagWrapper> wrapperTags = new ArrayList<>();
        List<Long> existTagIds = new ArrayList<>();
        Map<Long, FoodIngredientType> foodIngredientTypeMap = new HashMap<>();

        boolean isMain = false;

        for (FoodTagDto foodTag : foodTags) {
            if (Objects.isNull(foodTag.getId())) {
                try {
                    wrapperTags.add(new TagWrapper(tagService.saveTag(foodTag), foodTag.getTagUseType()));
                } catch (DataIntegrityViolationException | TagConflictException exp) {
                    wrapperTags.add(new TagWrapper(tagService.findByName(foodTag.getName()), foodTag.getTagUseType()));
                }
            } else {
                existTagIds.add(foodTag.getId());
                foodIngredientTypeMap.put(foodTag.getId(), foodTag.getTagUseType());
            }

            isMain = isMainIngredientType(isMain, foodTag);
        }

        if (!isMain) {
            throw new BadRequestException();
        }

        List<TagWrapper> existTags = tagService.findByIds(existTagIds).stream()
                .map(tag -> new TagWrapper(tag, foodIngredientTypeMap.get(tag.getId())))
                .collect(Collectors.toList());
        wrapperTags.addAll(existTags);

        return wrapperTags;
    }

    private boolean isMainIngredientType(boolean isMain, FoodTagDto foodTags) {
        if (foodTags.getTagUseType() == FoodIngredientType.MAIN) {
            return true;
        }

        return isMain;
    }
}
