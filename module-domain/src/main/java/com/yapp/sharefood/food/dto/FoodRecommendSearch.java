package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.flavor.domain.Flavor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class FoodRecommendSearch {
    private final int top;
    private final LocalDateTime before;
    private final LocalDateTime now;
    private final List<Flavor> flavors;
    private final List<Category> categories;

    public FoodRecommendSearch(int top, LocalDateTime before, LocalDateTime now, List<Flavor> flavors, List<Category> categories) {
        this.top = top;
        this.before = before;
        this.now = now;
        this.flavors = flavors;
        this.categories = categories;
    }
}
