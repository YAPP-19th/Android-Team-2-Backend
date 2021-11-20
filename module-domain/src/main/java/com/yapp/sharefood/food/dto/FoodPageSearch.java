package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.common.order.SortType;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.tag.domain.Tag;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodPageSearch {
    private Integer minPrice;

    private Integer maxPrice;

    private SortType sort;

    private FoodOrderType order;

    private List<Flavor> flavors;

    private List<Tag> tags;

    private Category category;

    private long lastCurosr;

    private int size;

    @Builder
    public FoodPageSearch(int minPrice, int maxPrice, String sort, String order, List<Flavor> flavors, List<Tag> tags, Category category, long lastCurosr, int size) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.flavors = flavors;
        this.tags = tags;
        this.category = category;
        this.sort = SortType.of(sort);
        this.order = FoodOrderType.of(order);
        this.lastCurosr = lastCurosr;
        this.size = size;
    }
}
