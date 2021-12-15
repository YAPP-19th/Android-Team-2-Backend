package com.yapp.sharefood.favorite.repository;

import com.yapp.sharefood.favorite.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
