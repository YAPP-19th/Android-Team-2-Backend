package com.yapp.sharefood.food.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.common.exception.BadRequestException;
import com.yapp.sharefood.food.domain.FoodIngredientType;
import com.yapp.sharefood.food.domain.TagWrapper;
import com.yapp.sharefood.food.dto.FoodTagDto;
import com.yapp.sharefood.food.dto.request.FoodCreationRequest;
import com.yapp.sharefood.food.service.FoodService;
import com.yapp.sharefood.tag.exception.TagConflictException;
import com.yapp.sharefood.tag.service.TagService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class FoodController {

    private static final int MAX_PIC_SIZE = 4;

    private final FoodService foodService;
    private final TagService tagService;

    @PostMapping("/api/v1/foods")
    public ResponseEntity<URI> saveFood(@AuthUser User user,
                                        @RequestPart("images") List<MultipartFile> images,
                                        @Valid @RequestPart("food") FoodCreationRequest foodCreationRequest) {
        if (images.size() > MAX_PIC_SIZE) {
            throw new BadRequestException();
        }

        List<TagWrapper> wrapperTags = getSavedWrapperTags(foodCreationRequest.getTags());

        URI userCreateUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(foodService.saveFood(user, foodCreationRequest, wrapperTags, images))
                .toUri();

        return ResponseEntity.created(userCreateUri).build();
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

            isMain = isMainIngredentType(isMain, foodTag);
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

    private boolean isMainIngredentType(boolean isMain, FoodTagDto foodTags) {
        if (foodTags.getTagUseType() == FoodIngredientType.MAIN) {
            return true;
        }

        return isMain;
    }
}
