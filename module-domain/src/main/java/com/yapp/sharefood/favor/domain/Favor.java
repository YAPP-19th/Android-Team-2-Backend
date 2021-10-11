package com.yapp.sharefood.favor.domain;

import javax.persistence.*;

@Entity
public class Favor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favor_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private FavorType tasteType;
}
