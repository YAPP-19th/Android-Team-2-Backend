package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.favor.domain.Favor;

import javax.persistence.*;

@Entity
public class FoodFavor extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_favor_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favor_id")
    private Favor favor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;
}
