package com.yapp.sharefood.flavor.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Flavor {
    @Id
    @Column(name = "flavor_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private FlavorType flavorType;

    private Flavor(FlavorType flavorType) {
        this.flavorType = flavorType;
    }

    public static Flavor of(FlavorType flavorType) {
        return new Flavor(flavorType);
    }
}
