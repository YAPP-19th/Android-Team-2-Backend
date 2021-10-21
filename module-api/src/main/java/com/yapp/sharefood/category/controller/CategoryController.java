package com.yapp.sharefood.category.controller;

import com.yapp.sharefood.category.dto.response.CategoriesTreeResponse;
import com.yapp.sharefood.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/api/v1/categories")
    public ResponseEntity<CategoriesTreeResponse> findAllTreeCategories() {
        return ResponseEntity.ok(categoryService.findAllTreeCategories());
    }
}
