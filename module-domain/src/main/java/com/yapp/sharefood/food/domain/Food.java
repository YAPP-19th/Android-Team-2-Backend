package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.category.Category;
import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.user.domain.User;

import javax.persistence.*;
import java.util.List;

@Entity
public class Food extends BaseEntity {
    @Id
    @Column(name = "food_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String foodTitle;

    private int price;

    private String reviewMsg; // @Log는 너무 낭비... -> 255로 설정

    @Enumerated(EnumType.STRING)
    private FoodStatus foodStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // mapped by
    @OneToMany(mappedBy = "food")
    private List<IngredientTag> ingredientTags;

    @OneToMany(mappedBy = "food")
    private List<FoodFavor> tasteItems;

    @OneToOne(mappedBy = "food")
    private FoodImages foodImages;
}
