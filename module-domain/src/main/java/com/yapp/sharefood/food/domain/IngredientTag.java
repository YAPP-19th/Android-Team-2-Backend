package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.tag.domain.Tag;

import javax.persistence.*;

@Entity
public class IngredientTag {
    @Id
    @Column(name = "ingredient_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Enumerated(EnumType.STRING)
    private IngredientUseType ingredientUseType;
}
