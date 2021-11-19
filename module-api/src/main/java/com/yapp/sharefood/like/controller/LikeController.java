package com.yapp.sharefood.like.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.like.service.LikeService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/api/v1/foods/{foodId}/likes")
    public ResponseEntity<URI> createLike(@AuthUser User user,
                                          @PathVariable("foodId") Long foodId,
                                          @RequestParam("categoryName") String categoryName) {

        URI likeUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(likeService.saveLike(user, foodId, categoryName))
                .toUri();
        return ResponseEntity.created(likeUri).build();
    }

    @DeleteMapping("/api/v1/foods/{foodId}/likes")
    public ResponseEntity<Void> deleteLike(@AuthUser User user,
                                           @PathVariable("foodId") Long foodId,
                                           @RequestParam("categoryName") String categoryName) {
        likeService.deleteLike(user, foodId, categoryName);
        return ResponseEntity.ok().build();
    }
}
