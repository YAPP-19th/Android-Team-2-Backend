package com.yapp.sharefood.user.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.flavor.dto.request.UserFlavorRequest;
import com.yapp.sharefood.flavor.dto.response.FlavorsResponse;
import com.yapp.sharefood.flavor.dto.response.UpdateUserFlavorResponse;
import com.yapp.sharefood.flavor.service.FlavorService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserFlavorController {
    private final FlavorService flavorService;

    @GetMapping("/api/v1/users/me/flavors")
    public ResponseEntity<FlavorsResponse> findUserFlavor(@AuthUser User user) {
        return ResponseEntity.ok(flavorService.findUserFlavors(user));
    }

    @PutMapping("/api/v1/users/me/flavors")
    public ResponseEntity<FlavorsResponse> updateUserFlavor(@AuthUser User user,
                                                                     @Valid @RequestBody UserFlavorRequest request) {
        return ResponseEntity.ok(flavorService.updateUserFlavors(user, request));
    }
}
