package com.yapp.sharefood.food.dto.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class FoodPageSearchRequest {
    @Min(0)
    private Integer minPrice;
    @Min(0)
    private Integer maxPrice;

    private List<String> tags = new ArrayList<>();

    private String sort;
    private String order;

    @NotNull
    @NotBlank
    private String categoryName;

    @NotNull
    @NotBlank
    private Long offset;

    @NotNull
    private Integer pageSize;
}
