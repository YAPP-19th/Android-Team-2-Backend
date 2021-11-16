package com.yapp.sharefood.food.dto.request;

import com.yapp.sharefood.flavor.dto.FlavorDto;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.dto.FoodTagDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodCreationRequest {
    // step 1
    @NotBlank
    @NotNull
    private String categoryName;

    // step 2
    @NotNull
    @NotBlank
    private String title;

    @NotNull
    private List<FoodTagDto> tags;

    @NotNull
    private Integer price;

    @NotNull
    private List<FlavorDto> flavors;

    @NotNull
    @Size(max = 3)
    private List<MultipartFile> images;

    @NotNull
    @NotBlank
    private String reviewMsg;

    @NotNull
    private FoodStatus foodStatus;

    @Builder
    public FoodCreationRequest(String categoryName, String title, List<FoodTagDto> tags, Integer price, List<FlavorDto> flavors, List<MultipartFile> images, String reviewMsg, FoodStatus foodStatus) {
        this.categoryName = categoryName;
        this.title = title;
        this.tags = tags;
        this.price = price;
        this.flavors = flavors;
        this.images = images;
        this.reviewMsg = reviewMsg;
        this.foodStatus = foodStatus;
    }
}
