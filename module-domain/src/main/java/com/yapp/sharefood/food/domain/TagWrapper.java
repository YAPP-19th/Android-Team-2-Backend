package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.tag.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TagWrapper {
    private Tag tag;
    private FoodIngredientType ingredientType;
}
