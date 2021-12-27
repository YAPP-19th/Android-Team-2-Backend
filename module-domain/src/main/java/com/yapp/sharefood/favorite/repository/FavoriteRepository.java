package com.yapp.sharefood.favorite.repository;

import com.yapp.sharefood.favorite.domain.Favorite;
import com.yapp.sharefood.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findAllByUser(User user);
}
