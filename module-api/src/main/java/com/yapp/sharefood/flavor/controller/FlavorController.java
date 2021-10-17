package com.yapp.sharefood.flavor.controller;

import com.yapp.sharefood.flavor.dto.response.FlavorsResponse;
import com.yapp.sharefood.flavor.service.FlavorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FlavorController {
    private final FlavorService flavorService;

    @GetMapping("/api/v1/favors")
    public ResponseEntity<FlavorsResponse> findAllFlavors() {
        FlavorsResponse flavorsResponse = new FlavorsResponse(flavorService.findAllFlavors());

        return ResponseEntity.ok(flavorsResponse);
    }
}
