package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.tag.domain.Tag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodTag extends BaseEntity {
    @Id
    @Column(name = "food_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private FoodIngredientType ingredientType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public boolean isSameTag(Tag tag) {
        return Objects.equals(this.tag, tag);
    }

    public FoodTag(FoodIngredientType ingredientType, Food food, Tag tag) {
        this.ingredientType = ingredientType;
        this.food = food;
        this.tag = tag;
    }
}
