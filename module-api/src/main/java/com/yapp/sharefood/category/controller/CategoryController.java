package com.yapp.sharefood.category.controller;

import com.yapp.sharefood.category.dto.response.CategoryAllResponse;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {

    @GetMapping("/api/v1/categies")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 전체 카테고리 정보 tree 형태로 반환", response = CategoryAllResponse.class)
    })
    public ResponseEntity<CategoryAllResponse> findAllCategories() {
        CategoryAllResponse categoryAllResponse = new CategoryAllResponse();
        return ResponseEntity.ok(categoryAllResponse);
    }
}
