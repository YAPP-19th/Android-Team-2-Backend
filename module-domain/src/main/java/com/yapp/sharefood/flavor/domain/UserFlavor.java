package com.yapp.sharefood.flavor.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.user.domain.User;

import javax.persistence.*;

@Entity
public class UserFlavor extends BaseEntity {
    @Id
    @Column(name = "user_flavor_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flavor_id")
    private Flavor flavor;
}
