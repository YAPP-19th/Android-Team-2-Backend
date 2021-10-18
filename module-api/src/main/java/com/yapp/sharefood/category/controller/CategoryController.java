package com.yapp.sharefood.category.controller;

import com.yapp.sharefood.category.dto.CategoryDto;
import com.yapp.sharefood.category.dto.response.CategoryAllResponse;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    @GetMapping("/api/v1/categies")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 전체 카테고리 정보 tree 형태로 반환", response = CategoryAllResponse.class)
    })
    public ResponseEntity<CategoryAllResponse> findAllCategories() {
        CategoryAllResponse categoryAllResponse = new CategoryAllResponse();
        List<CategoryDto> cafeDto = List.of(new CategoryDto(2L, "이디아", null),
                new CategoryDto(3L, "공차", null),
                new CategoryDto(4L, "스타벅스", null));

        List<CategoryDto> foodDto = List.of(new CategoryDto(6L, "마라탕", null),
                new CategoryDto(7L, "샌드위치", null),
                new CategoryDto(8L, "요플레", null));

        categoryAllResponse.setCategories(List.of(new CategoryDto(1L, "커피", cafeDto), new CategoryDto(5L, "음식", foodDto)));

        return ResponseEntity.ok(categoryAllResponse);
    }
}
