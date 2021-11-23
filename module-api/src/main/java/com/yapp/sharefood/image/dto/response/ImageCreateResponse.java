package com.yapp.sharefood.image.dto.response;

import com.yapp.sharefood.image.dto.ImageDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageCreateResponse {
    private List<ImageDto> images;
}
