package com.yapp.sharefood.food.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class FoodPageSearchRequest {
    @Min(0)
    private Integer minPrice;
    @Min(0)
    private Integer maxPrice;

    private List<String> tags = new ArrayList<>();
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
}
