package com.yapp.sharefood.category.domain;

import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChildCategories {
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> childCategories = new ArrayList<>();

    public void removeChildCategory(Long id) {
        Category findCategoryForRemoving = findById(id);
        childCategories.remove(findCategoryForRemoving);
    }

    public void assignChildCategory(Category newChild, Category parent) {
        if (Objects.isNull(newChild)) {
            throw new CategoryNotFoundException();
        }

        newChild.assignParent(parent);
        if (!childCategories.contains(newChild)) {
            childCategories.add(newChild);
        }
    }

    private Category findById(Long categoryId) {
        return childCategories.stream()
                .filter(category -> category.getId().equals(categoryId))
                .findAny()
                .orElseThrow(CategoryNotFoundException::new);
    }
}
