package com.yapp.sharefood.like.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.like.service.LikeService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/api/v1/categories/{categoryName}/foods/{foodId}/likes")
    public ResponseEntity<URI> createLike(@AuthUser User user,
                                          @PathVariable("foodId") Long foodId,
                                          @PathVariable("categoryName") String categoryName) {

        URI likeUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(likeService.saveLike(user, foodId, categoryName))
                .toUri();
        return ResponseEntity.created(likeUri).build();
    }
}
