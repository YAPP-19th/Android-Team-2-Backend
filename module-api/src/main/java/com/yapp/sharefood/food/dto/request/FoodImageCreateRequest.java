package com.yapp.sharefood.food.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class FoodImageCreateRequest {
    @Size(max = 3)
    @NotNull
    private List<MultipartFile> images;
}
