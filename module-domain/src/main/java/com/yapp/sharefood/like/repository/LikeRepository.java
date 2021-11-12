package com.yapp.sharefood.like.repository;

import com.yapp.sharefood.like.domain.Like;
import com.yapp.sharefood.like.repository.custom.LikeCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeCustomRepository {
}
