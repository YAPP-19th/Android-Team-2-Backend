package com.yapp.sharefood.food.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.food.domain.FoodIngredientType;
import com.yapp.sharefood.food.domain.TagWrapper;
import com.yapp.sharefood.food.dto.FoodTagDto;
import com.yapp.sharefood.food.dto.request.FoodCreationRequest;
import com.yapp.sharefood.food.service.FoodService;
import com.yapp.sharefood.tag.service.TagService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;
    private final TagService tagService;

    @PostMapping("/api/v1/foods")
    public ResponseEntity<URI> saveFood(@AuthUser User user,
                                        @Valid @RequestBody FoodCreationRequest foodCreationRequest) {
        List<TagWrapper> wrapperTags = new ArrayList<>();
        List<Long> existTagIds = new ArrayList<>();
        Map<Long, FoodIngredientType> foodIngredientTypeMap = new HashMap<>();

        for (FoodTagDto foodTags : foodCreationRequest.getTags()) {
            if (Objects.isNull(foodTags.getId())) {
                try {
                    wrapperTags.add(new TagWrapper(tagService.saveTag(foodTags), foodTags.getTagUseType()));
                } catch (DataIntegrityViolationException e) {
                    wrapperTags.add(new TagWrapper(tagService.findByName(foodTags.getName()), foodTags.getTagUseType()));
                }
            } else {
                existTagIds.add(foodTags.getId());
                foodIngredientTypeMap.put(foodTags.getId(), foodTags.getTagUseType());
            }
        }
        List<TagWrapper> existTags = tagService.findByIds(existTagIds).stream()
                .map(tag -> new TagWrapper(tag, foodIngredientTypeMap.get(tag.getId())))
                .collect(Collectors.toList());
        wrapperTags.addAll(existTags);

        URI userCreateUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(foodService.saveFood(user, foodCreationRequest, wrapperTags))
                .toUri();

        return ResponseEntity.created(userCreateUri).build();
    }
}
