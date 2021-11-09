package com.yapp.sharefood.like.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.user.domain.User;

import javax.persistence.*;

@Entity
@Table(name = "likes")
public class Like extends BaseEntity {
    @Id
    @Column(name = "like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
