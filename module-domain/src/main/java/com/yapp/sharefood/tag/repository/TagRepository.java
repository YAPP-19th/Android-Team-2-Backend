package com.yapp.sharefood.tag.repository;

import com.yapp.sharefood.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
