package com.yapp.sharefood.flavor.controller;

import com.yapp.sharefood.flavor.dto.response.FlavorResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlavorController {

    @GetMapping("/api/v1/flavors")
    @ApiOperation("입맛 정보 전체 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 입 맛 정보 반환", response = FlavorResponse.class)
    })
    public ResponseEntity<FlavorResponse> findAllTypeFlavors() {
        FlavorResponse flavorResponse = new FlavorResponse();
        return ResponseEntity.ok(flavorResponse);
    }
}