package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.user.domain.User;

import javax.persistence.*;

@Entity
public class Food {
    @Id
    @Column(name = "food_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String foodTitle;

    @Column(nullable = false)
    private int price;

    private String reviewMsg;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodStatus foodStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;
}
