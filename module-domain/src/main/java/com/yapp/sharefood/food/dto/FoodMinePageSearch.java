package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.common.order.SortType;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.food.domain.FoodStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class FoodMinePageSearch {
    private final Integer minPrice;
    private final Integer maxPrice;

    private final List<Flavor> flavors;

    private final SortType sort;
    private final OrderType order;

    private final List<Category> categories;

    private final Long offset;

    private final Integer size;

    private final FoodStatus status;

    private final LocalDateTime searchTime;

    private final boolean isFlavorSearchExist;

    @Builder
    public FoodMinePageSearch(Integer minPrice, Integer maxPrice, List<Flavor> flavors, SortType sort, OrderType order, FoodStatus status, boolean isFlavorSearchExist,
                              List<Category> categories, Long offset, Integer size, LocalDateTime searchTime) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.flavors = flavors;
        this.sort = sort;
        this.order = order;
        this.status = status;
        this.categories = categories;
        this.offset = offset;
        this.size = size;
        this.searchTime = searchTime;
        this.isFlavorSearchExist = isFlavorSearchExist;
    }
}
