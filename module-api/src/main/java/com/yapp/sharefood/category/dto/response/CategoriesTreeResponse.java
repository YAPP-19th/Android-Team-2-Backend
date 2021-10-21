package com.yapp.sharefood.category.dto.response;

import com.yapp.sharefood.category.dto.CategoryDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoriesTreeResponse {
    private List<CategoryDto> categories;
}
