package com.yapp.sharefood.food.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodImageCreateRequest {
    @Size(min = 1, max = 3)
    @NotNull
    private List<MultipartFile> images;

    public static FoodImageCreateRequest of(List<MultipartFile> images) {
        return new FoodImageCreateRequest(images);
    }
}
