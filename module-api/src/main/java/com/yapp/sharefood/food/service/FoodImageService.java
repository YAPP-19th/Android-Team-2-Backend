package com.yapp.sharefood.food.service;

import com.yapp.sharefood.external.s3.StorageUploader;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.dto.FoodImageDto;
import com.yapp.sharefood.food.dto.response.FoodImageCreateResponse;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.image.domain.Image;
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
public class FoodImageService {
    private static final String FOOD_FILE_PATH = "food";

    private final FoodRepository foodRepository;
    private final ImageRepository imageRepository;
    private final StorageUploader storageUploader;

    @Transactional
    public FoodImageCreateResponse saveImages(Long foodId, List<MultipartFile> inputImages) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(FoodNotFoundException::new);
        List<Image> images = uploadImage(inputImages);
        food.getImages().addImages(images, food);

        return new FoodImageCreateResponse(FoodImageDto.toList(images));
    }


    private List<Image> uploadImage(List<MultipartFile> inputImages) {
        List<Image> images = new ArrayList<>();
        if (inputImages == null || inputImages.isEmpty()) return images;

        for (MultipartFile file : inputImages) {
            String uploadFileName = storageUploader.upload(FOOD_FILE_PATH, file);
            Image image = Image.builder()
                    .realFilename(file.getOriginalFilename())
                    .storeFilename(uploadFileName)
                    .build();
            images.add(imageRepository.save(image));
        }

        return images;
    }

    public void deleteImages(List<Image> foodImages) {
        foodImages.forEach(image -> storageUploader.delete(FOOD_FILE_PATH, image.getStoreFilename()));
    }
}
