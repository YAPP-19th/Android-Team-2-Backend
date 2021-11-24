package com.yapp.sharefood.image.repository;

import com.yapp.sharefood.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
