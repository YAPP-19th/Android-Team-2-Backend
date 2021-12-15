package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.tag.domain.Tag;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Embeddable
public class FoodTags {
    @OneToMany(mappedBy = "food", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<FoodTag> foodTags = new ArrayList<>();

    public void addAllTags(List<TagWrapper> wrapperTags, Food food) {
        wrapperTags.forEach(wrapperTag -> validateDuplicateTags(wrapperTag.getTag()));

        wrapperTags.stream()
                .map(wrapperTag -> new FoodTag(wrapperTag.getIngredientType(), food, wrapperTag.getTag()))
                .forEach(foodTags::add);
    }

    private void validateDuplicateTags(Tag tag) {
        if (foodTags.stream().anyMatch(foodTag -> foodTag.isSameTag(tag))) {
            throw new InvalidOperationException("이미 등록된 tag입니다.");
        }
    }

    public void updateTags(List<TagWrapper> wrappersTag, Food food) {
        Map<Long, TagWrapper> tagMap = wrappersTag.stream().collect(Collectors.toMap(tagWrapper -> tagWrapper.getTag().getId(), tagWrapper -> tagWrapper));

        // remove
        foodTags.removeIf(foodTag -> !tagMap.containsKey(foodTag.getTag().getId()) || tagMap.get(foodTag.getTag().getId()).getIngredientType() != foodTag.getIngredientType());

        Map<Long, FoodTag> foodTagMap = foodTags.stream().collect(Collectors.toMap(foodTag -> foodTag.getTag().getId(), foodTag -> foodTag));
        addAllTags(tagMap.entrySet().stream()
                .filter(tagEntry -> !foodTagMap.containsKey(tagEntry.getKey()) || tagEntry.getValue().getIngredientType() != foodTagMap.get(tagEntry.getKey()).getIngredientType())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()), food);
    }
}
