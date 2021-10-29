package com.yapp.sharefood.flavor.controller;

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

import static com.yapp.sharefood.auth.utils.AuthValidationUtils.validateUserIdPath;

@RestController
@RequiredArgsConstructor
public class FlavorController {
    private final FlavorService flavorService;

    @GetMapping("/api/v1/flavors")
    public ResponseEntity<FlavorsResponse> findAllFlavors() {
        return ResponseEntity.ok(flavorService.findAllFlavors());
    }

    @PutMapping("/api/v1/users/{userId}/flavors/")
    public ResponseEntity<UpdateUserFlavorResponse> updateUserFlavor(@AuthUser User user,
                                                                     @PathVariable("userId") Long userId,
                                                                     @Valid @RequestBody UserFlavorRequest request) {
        validateUserIdPath(userId, user);

        return ResponseEntity.ok(flavorService.updateUserFlavors(user, request));
    }
}
