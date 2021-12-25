package com.yapp.sharefood.favorite.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.favorite.dto.response.FavoriteFoodResponse;
import com.yapp.sharefood.favorite.service.FavoriteService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping("/api/v1/foods/favorite")
    public ResponseEntity<FavoriteFoodResponse> findFavorite(@AuthUser User user) {
        return ResponseEntity.ok(favoriteService.findFavoriteFoods(user));
    }

    @PostMapping("/api/v1/foods/{foodId}/favorite")
    public ResponseEntity<URI> createFavorite(@AuthUser User user, @PathVariable("foodId") Long foodId) {
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(favoriteService.createFavorite(user, foodId))
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/api/v1/foods/{foodId}/favorite")
    public ResponseEntity<Void> deleteFavorite(@AuthUser User user, @PathVariable("foodId") Long foodId) {
        favoriteService.deleteFavorite(user, foodId);
        return ResponseEntity.ok().build();
    }
}
