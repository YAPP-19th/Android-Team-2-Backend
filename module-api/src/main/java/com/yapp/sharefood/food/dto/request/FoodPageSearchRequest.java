package com.yapp.sharefood.food.dto.request;

import com.yapp.sharefood.flavor.dto.FlavorDto;
import com.yapp.sharefood.tag.dto.TagDto;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class FoodPageSearchRequest {
    @Min(0)
    private Integer minPrice;
    @Min(0)
    private Integer maxPrice;

    @NotNull
    List<FlavorDto> flavors;

    @NotNull
    List<TagDto> tags;
}
