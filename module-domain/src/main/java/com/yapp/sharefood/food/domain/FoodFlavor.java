package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.flavor.domain.Flavor;

import javax.persistence.*;

@Entity
public class FoodFlavor extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_flavor_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flavor_id")
    private Flavor flavor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;
}
