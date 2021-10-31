package com.yapp.sharefood.tag.repository;

import com.yapp.sharefood.tag.domain.Tag;

import java.util.List;

public interface TagQueryRepository {
    List<Tag> findSimilarTags(String similarName, int size);
}
