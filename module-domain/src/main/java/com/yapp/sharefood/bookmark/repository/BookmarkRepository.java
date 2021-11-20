package com.yapp.sharefood.bookmark.repository;

import com.yapp.sharefood.bookmark.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
