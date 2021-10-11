package com.yapp.sharefood.like.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.user.domain.User;

import javax.persistence.*;

@Entity
@Table(name = "likes")
public class Like extends BaseEntity {
    @Id
    @Column(name = "likes_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;
}
