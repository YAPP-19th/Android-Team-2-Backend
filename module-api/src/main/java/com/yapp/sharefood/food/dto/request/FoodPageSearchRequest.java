package com.yapp.sharefood.food.dto.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class FoodPageSearchRequest {
    @Min(0)
    private Integer minPrice;
    @Min(0)
    private Integer maxPrice;

    @NotNull
    private List<String> tags;

    private String sort;
    private String order;

    @NotNull
    @NotBlank
    private String categoryName;

    @NotNull
    private Long cursor;

    @NotNull
    private Integer size;
}
