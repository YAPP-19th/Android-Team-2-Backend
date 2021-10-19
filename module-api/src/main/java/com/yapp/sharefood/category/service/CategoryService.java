package com.yapp.sharefood.category.service;

import com.yapp.sharefood.category.dto.CategoryDto;
import com.yapp.sharefood.category.dto.response.CategoriesTreeResponse;
import com.yapp.sharefood.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoriesTreeResponse findAllTreeCategories() {
        List<CategoryDto> categories = categoryRepository.findAll()
                .stream().map(category -> CategoryDto.of(category.getId(), category.getName(), null))
                .collect(Collectors.toList());
        return new CategoriesTreeResponse(categories);
    }
}
