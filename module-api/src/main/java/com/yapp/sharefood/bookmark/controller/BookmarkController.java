package com.yapp.sharefood.bookmark.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.bookmark.service.BookmarkService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping("/api/v1/foods/{foodId}/bookmark")
    public ResponseEntity<Long> createBookmark(@AuthUser User user, @PathVariable("foodId") Long foodId) {
        Long id = bookmarkService.saveBookmark(user, foodId);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/api/v1/foods/{foodId}/bookmark")
    public ResponseEntity<Void> deleteBookmark(@AuthUser User user, @PathVariable("foodId") Long foodId) {
        bookmarkService.deleteBookmark(user, foodId);
        return ResponseEntity.ok().build();
    }
}
