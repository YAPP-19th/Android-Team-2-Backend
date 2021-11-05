package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.tag.domain.Tag;
import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Embeddable
public class FoodTags {
    @OneToMany(mappedBy = "food")
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
}
