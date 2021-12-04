package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.tag.domain.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TagWrapper {
    private Tag tag;
    private FoodIngredientType ingredientType;
}
