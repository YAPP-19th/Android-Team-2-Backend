package com.yapp.sharefood.image.controller;

import com.yapp.sharefood.image.dto.request.ImageCreateRequest;
import com.yapp.sharefood.image.dto.response.ImageCreateResponse;
import com.yapp.sharefood.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/api/v1/foods/images")
    public ResponseEntity<ImageCreateResponse> saveImages(@Valid ImageCreateRequest images) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(imageService.saveImages(images.getImages()));
    }
}
