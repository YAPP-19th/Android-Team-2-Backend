package com.yapp.sharefood.tag.repository;

import com.yapp.sharefood.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long>, TagQueryRepository {
    Optional<Tag> findByName(String name);

    List<Tag> findByIdIn(List<Long> ids);

    List<Tag> findByNameIn(List<String> tag);
}
