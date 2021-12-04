package com.yapp.sharefood.food.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodPageSearchRequest {
    @Min(0)
    private Integer minPrice;
    @Min(0)
    private Integer maxPrice;

    @NotNull
    private List<String> tags = new ArrayList<>();
    @NotNull
    private List<String> flavors = new ArrayList<>();

    private String sort;
    private String order;

    @NotNull
    @NotBlank
    private String categoryName;

    @NotNull
    private Long offset;

    @NotNull
    private Integer pageSize;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime firstSearchTime;


    @Builder
    public FoodPageSearchRequest(Integer minPrice, Integer maxPrice, List<String> tags, List<String> flavors, String sort, String order, String categoryName, Long offset, Integer pageSize, LocalDateTime firstSearchTime) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.tags = tags;
        this.flavors = flavors;
        this.sort = sort;
        this.order = order;
        this.categoryName = categoryName;
        this.offset = offset;
        this.pageSize = pageSize;
        this.firstSearchTime = firstSearchTime;
    }
}
