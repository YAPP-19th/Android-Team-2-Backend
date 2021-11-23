package com.yapp.sharefood.image.service;

import com.yapp.sharefood.external.s3.AwsS3Uploader;
import com.yapp.sharefood.image.domain.Image;
import com.yapp.sharefood.image.dto.ImageDto;
import com.yapp.sharefood.image.dto.response.ImageCreateResponse;
import com.yapp.sharefood.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {
    private static final String FOOD_FILE_PATH = "food";

    private final ImageRepository imageRepository;
    private final AwsS3Uploader awsS3Uploader;


    @Transactional
    public ImageCreateResponse saveImages(List<MultipartFile> inputImages) {
        return new ImageCreateResponse(ImageDto.toList(uploadImage(inputImages)));
    }


    private List<Image> uploadImage(List<MultipartFile> inputimages) {
        List<Image> images = new ArrayList<>();
        if (inputimages == null || inputimages.isEmpty()) return images;

        for (MultipartFile file : inputimages) {
            String uploadFileName = awsS3Uploader.upload(FOOD_FILE_PATH, file);
            Image image = Image.builder()
                    .realFilename(file.getOriginalFilename())
                    .storeFilename(uploadFileName)
                    .build();
            images.add(imageRepository.save(image));
        }

        return images;
    }
}
