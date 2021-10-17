package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.user.domain.User;

import javax.persistence.*;

@Entity
public class Food {
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
}
