package com.yapp.sharefood.user.domain;

import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.userflavor.domain.UserFlavor;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Embeddable
public class UserFlavors {
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<UserFlavor> userFlavors = new ArrayList<>();

    public void addAllFlavors(List<Flavor> flavors, User user) {
        for (Flavor flavor : flavors) {
            userFlavors.add(UserFlavor.of(user, flavor));
        }
    }

    public void deleteAllFlavors() {
        userFlavors.clear();
    }
}
