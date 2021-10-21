package com.yapp.sharefood.category.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.dto.CategoryDto;
import com.yapp.sharefood.category.dto.response.CategoriesTreeResponse;
import com.yapp.sharefood.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoriesTreeResponse findAllTreeCategories() {
        List<Category> allCategories = categoryRepository.findAll();

        List<Category> parentNotExistCategories = allCategories.stream()
                .filter(category -> Objects.isNull(category.getParent()))
                .collect(Collectors.toList());
        Map<Long, List<Category>> parentIdChildren = allCategories.stream()
                .filter(category -> category.getParent() != null)
                .collect(groupingBy(category -> category.getParent().getId()));

        return new CategoriesTreeResponse(createCategoryTree(parentNotExistCategories, parentIdChildren));
    }

    private List<CategoryDto> createCategoryTree(List<Category> parentNotExistCategories, Map<Long, List<Category>> parentIdChildren) {
        List<CategoryDto> treeCategories = new ArrayList<>();
        Set<Long> visitedId = new HashSet<>();

        Queue<CategoryDto> bfsQueue = new LinkedList<>();
        for (Category parentsNotExistCategory : parentNotExistCategories) {
            CategoryDto topParent = CategoryDto.of(parentsNotExistCategory.getId(), parentsNotExistCategory.getName(), new ArrayList<>());

            visitedId.add(topParent.getId());
            bfsQueue.add(topParent);
            treeCategories.add(topParent);
        }

        while (!bfsQueue.isEmpty()) {
            CategoryDto parent = bfsQueue.poll();
            List<Category> children = parentIdChildren.getOrDefault(parent.getId(), new ArrayList<>());

            for (Category child : children) { // 싸이클 재외 로직
                CategoryDto childCategoryDto = CategoryDto.of(child.getId(), child.getName(), new ArrayList<>());
                if (!visitedId.contains(childCategoryDto.getId())) {
                    parent.getChildCategories().add(childCategoryDto);
                    bfsQueue.add(childCategoryDto);
                }
            }
        }

        return treeCategories;
    }
}
