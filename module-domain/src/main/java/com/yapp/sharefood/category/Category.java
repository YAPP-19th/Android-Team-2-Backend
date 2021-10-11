package com.yapp.sharefood.category;

import com.yapp.sharefood.common.domain.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parent; // cafe / food

    @OneToMany(mappedBy = "parent")
    private List<Category> childCategories = new ArrayList<>(); // 공차, 이디야, 아이스빈..... 마라탕, 서브웨이 ....
}
