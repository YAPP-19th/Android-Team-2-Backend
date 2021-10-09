package com.yapp.sharefood.favor.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.user.domain.User;

import javax.persistence.*;

@Entity
public class UserFavor extends BaseEntity {
    @Id
    @Column(name = "user_favor_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favor_id")
    private Favor favor;
}
