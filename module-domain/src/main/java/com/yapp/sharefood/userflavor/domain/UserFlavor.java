package com.yapp.sharefood.userflavor.domain;

import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    private UserFlavor(User user, Flavor flavor) {
        this.user = user;
        this.flavor = flavor;
    }

    public static UserFlavor of(User user, Flavor flavor) {
        return new UserFlavor(user, flavor);
    }

    public boolean isSame(Flavor flavor) {
        return flavor.getFlavorType() == this.flavor.getFlavorType();
    }
}
