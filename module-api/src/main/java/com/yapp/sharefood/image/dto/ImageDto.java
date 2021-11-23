package com.yapp.sharefood.image.dto;

import com.yapp.sharefood.image.domain.Image;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageDto {
    private Long id;
    private String saveFileName;
    private String realFileName;

    public static ImageDto of(Long id, String saveFileName, String realFileName) {
        return new ImageDto(id, saveFileName, realFileName);
    }

    public static ImageDto toImageDto(Image image) {
        return of(image.getId(), image.getStoreFilename(), image.getRealFilename());
    }

    public static List<ImageDto> toList(List<Image> images) {
        return images.stream()
                .map(ImageDto::toImageDto)
                .collect(Collectors.toList());
    }
}
