package com.yapp.sharefood.userflavor.domain;

import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.user.domain.User;

import javax.persistence.*;

@Entity
public class UserFlavor {

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
