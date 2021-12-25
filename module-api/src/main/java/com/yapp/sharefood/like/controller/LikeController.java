package com.yapp.sharefood.like.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.config.lock.UserLevelLock;
import com.yapp.sharefood.like.service.LikeService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static com.yapp.sharefood.config.lock.UserlevelLockSql.DEFAULT_USERLEVEL_LOCk_TIME_OUT;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private static final String SERVICE_NAME = "like";

    private final LikeService likeService;
    private final UserLevelLock userlevelLock;

    @PostMapping("/api/v1/foods/{foodId}/likes")
    public ResponseEntity<URI> createLike(@AuthUser User user,
                                          @PathVariable("foodId") Long foodId) {

        Long likeId = userlevelLock.executeWithLock(
                SERVICE_NAME + "_" + foodId,
                DEFAULT_USERLEVEL_LOCk_TIME_OUT,
                () -> likeService.saveLike(user, foodId)
        );

        URI likeUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(likeId)
                .toUri();
        return ResponseEntity.created(likeUri).build();
    }

    @DeleteMapping("/api/v1/foods/{foodId}/likes")
    public ResponseEntity<Void> deleteLike(@AuthUser User user,
                                           @PathVariable("foodId") Long foodId) {
        userlevelLock.executeWithLock(
                SERVICE_NAME + "_" + foodId,
                DEFAULT_USERLEVEL_LOCk_TIME_OUT,
                () -> {
                    likeService.deleteLike(user, foodId);
                    return null;
                });
        return ResponseEntity.ok().build();
    }
}
