package com.yapp.sharefood.bookmark;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.user.domain.User;

import javax.persistence.*;

@Entity
public class BookMark extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_mark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
