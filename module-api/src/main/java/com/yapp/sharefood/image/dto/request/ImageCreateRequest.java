package com.yapp.sharefood.image.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ImageCreateRequest {
    @Max(3)
    @NotNull
    private List<MultipartFile> images;
}
