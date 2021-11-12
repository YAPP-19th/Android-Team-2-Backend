package com.yapp.sharefood.category.domain;

import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import com.yapp.sharefood.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "category_name_parents_id",
                columnNames = {"name", "parent_category_id"}
        )
})
public class Category extends BaseEntity {
    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parent;

    @Embedded
    private final ChildCategories childCategories = new ChildCategories();

    private Category(String name) {
        this.name = name;
    }

    public static Category of(String name) {
        return new Category(name);
    }

    public void assignParent(Category parent) {
        if (Objects.isNull(parent)) {
            throw new CategoryNotFoundException();
        }

        this.parent = parent;
        parent.childCategories.getChildCategories().add(this);
    }

    public void addChildCategories(Category... newChildren) {
        for (Category category : newChildren) {
            childCategories.assignChildCategory(category, this);
        }
    }
}
