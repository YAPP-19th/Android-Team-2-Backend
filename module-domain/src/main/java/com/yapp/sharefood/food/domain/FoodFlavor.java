package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.flavor.domain.Flavor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodFlavor extends BaseEntity {
    @Id
    @Column(name = "food_flavor_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flavor_id", nullable = false)
    private Flavor flavor;

    public boolean isSameFlavor(Flavor flavor) {
        return Objects.equals(this.flavor.getFlavorType(), flavor.getFlavorType());
    }

    public FoodFlavor(Food food, Flavor flavor) {
        this.food = food;
        this.flavor = flavor;
    }
}
