package com.yapp.sharefood.flavor.controller;

import com.yapp.sharefood.flavor.dto.response.FlavorsResponse;
import com.yapp.sharefood.flavor.service.FlavorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FlavorController {
    private final FlavorService flavorService;

    @GetMapping("/api/v1/flavors")
    public ResponseEntity<FlavorsResponse> findAllFlavors() {
        return ResponseEntity.ok(flavorService.findAllFlavors());
    }
}
