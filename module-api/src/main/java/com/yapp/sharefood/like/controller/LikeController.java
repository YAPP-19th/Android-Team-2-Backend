package com.yapp.sharefood.like.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.like.dto.request.LikeCreationRequest;
import com.yapp.sharefood.like.dto.request.LikeDeleteRequest;
import com.yapp.sharefood.like.service.LikeService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/api/v1/foods/{foodId}/likes")
    public ResponseEntity<URI> createLike(@AuthUser User user,
                                          @PathVariable("foodId") Long foodId,
                                          @Valid @RequestBody LikeCreationRequest likeCreationRequest) {

        URI likeUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(likeService.saveLike(user, foodId, likeCreationRequest.getCategoryName()))
                .toUri();
        return ResponseEntity.created(likeUri).build();
    }

    @DeleteMapping("/api/v1/foods/{foodId}/likes")
    public ResponseEntity<Void> deleteLike(@AuthUser User user,
                                           @PathVariable("foodId") Long foodId,
                                           @Valid @RequestBody LikeDeleteRequest likeDeleteRequest) {
        likeService.deleteLike(user, foodId, likeDeleteRequest.getCategoryName());
        return ResponseEntity.ok().build();
    }
}
