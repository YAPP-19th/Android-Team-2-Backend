package com.yapp.sharefood.food.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.common.StubFileUploader;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.dto.response.FoodImageCreateResponse;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.image.repository.ImageRepository;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class FoodImageServiceTest {
    FoodImageService foodImageService;

    @Autowired
    FoodRepository foodRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        foodImageService = new FoodImageService(foodRepository, imageRepository, new StubFileUploader());
    }

    @Test
    void saveImages_Success() {
        // given
        User saveUser = userRepository.save(User.builder()
                .oauthId("1234")
                .name("name")
                .nickname("owaowa")
                .oAuthType(OAuthType.KAKAO)
                .build());
        Category category = categoryRepository.save(Category.of("category"));
        Food food = Food.builder()
                .foodTitle("title")
                .reviewMsg("msg")
                .writer(saveUser)
                .category(category)
                .foodStatus(FoodStatus.SHARED)
                .build();
        Food saveFood = foodRepository.save(food);
        List<MultipartFile> files = List.of(new MockMultipartFile("name", new byte[]{}));

        // when
        FoodImageCreateResponse foodImageCreateResponse = foodImageService.saveImages(saveFood.getId(), files);

        // then
        assertThat(foodImageCreateResponse.getImages()).hasSize(1);
        assertEquals(StubFileUploader.UPLOAD_FILE_URL, foodImageCreateResponse.getImages().get(0).getImageUrl());
    }

    @Test
    void saveImageFoodNotExist_404NotFoundError() throws Exception {
        // given
        List<MultipartFile> files = List.of(new MockMultipartFile("name", new byte[]{}));

        // when

        // then
        assertThrows(FoodNotFoundException.class, () -> foodImageService.saveImages(1L, files));
    }
}