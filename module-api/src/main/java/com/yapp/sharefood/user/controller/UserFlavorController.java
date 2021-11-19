package com.yapp.sharefood.user.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.flavor.dto.request.UserFlavorRequest;
import com.yapp.sharefood.flavor.dto.response.FlavorsResponse;
import com.yapp.sharefood.flavor.dto.response.UpdateUserFlavorResponse;
import com.yapp.sharefood.flavor.service.FlavorService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.yapp.sharefood.auth.utils.AuthUtils.validateUserIdPath;

@RestController
@RequiredArgsConstructor
public class UserFlavorController {
    private final FlavorService flavorService;

    @GetMapping("/api/v1/users/{userId}/flavors")
    public ResponseEntity<FlavorsResponse> findUserFlavor(@AuthUser User user,
                                                          @PathVariable("userId") Long userId) {
        validateUserIdPath(userId, user);

        return ResponseEntity.ok(flavorService.findUserFlavors(user));
    }

    @PutMapping("/api/v1/users/{userId}/flavors")
    public ResponseEntity<UpdateUserFlavorResponse> updateUserFlavor(@AuthUser User user,
                                                                     @PathVariable("userId") Long userId,
                                                                     @Valid @RequestBody UserFlavorRequest request) {
        validateUserIdPath(userId, user);

        return ResponseEntity.ok(flavorService.updateUserFlavors(user, request));
    }
}