package com.yapp.sharefood.category.dto.response;

import com.yapp.sharefood.category.dto.CategoryDto;
import lombok.Data;

import java.util.List;

@Data
public class CategoryAllResponse {
    private List<CategoryDto> categories;
}
