package com.yapp.sharefood.flavor.domain;

import javax.persistence.*;

@Entity
public class Flavor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flavor_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private FlavorType tasteType;
}
