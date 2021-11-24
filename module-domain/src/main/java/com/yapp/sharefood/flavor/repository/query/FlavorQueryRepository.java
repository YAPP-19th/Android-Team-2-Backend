package com.yapp.sharefood.flavor.repository.query;

import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.user.domain.User;

import java.util.List;

public interface FlavorQueryRepository {
    List<Flavor> findByUser(User user);
}
