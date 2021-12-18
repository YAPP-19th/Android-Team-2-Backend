package com.yapp.sharefood.food.dto.request;

import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.dto.MineFoodType;
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
public class FoodMinePageSearchRequest {
    @Min(0)
    private Integer minPrice;
    @Min(0)
    private Integer maxPrice;

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

    private FoodStatus status;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime firstSearchTime;

    @NotNull
    private MineFoodType mineFoodType;

    @Builder
    public FoodMinePageSearchRequest(Integer minPrice, Integer maxPrice, List<String> flavors, String sort, String order, FoodStatus status,
                                     String categoryName, Long offset, Integer pageSize, LocalDateTime firstSearchTime, MineFoodType mineFoodType) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.flavors = flavors;
        this.sort = sort;
        this.order = order;
        this.status = status;
        this.categoryName = categoryName;
        this.offset = offset;
        this.pageSize = pageSize;
        this.firstSearchTime = firstSearchTime;
        this.mineFoodType = mineFoodType;
    }
}
