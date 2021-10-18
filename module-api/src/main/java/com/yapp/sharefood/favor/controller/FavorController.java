package com.yapp.sharefood.favor.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.favor.dto.request.MeFavorRequest;
import com.yapp.sharefood.favor.dto.response.FavorResponse;
import com.yapp.sharefood.user.domain.User;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class FavorController {

    @GetMapping("/api/v1/flavors")
    @ApiOperation("입맛 정보 전체 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 입 맛 정보 반환", response = FavorResponse.class)
    })
    public ResponseEntity<FavorResponse> findAllTypeFavors() {
        FavorResponse favorResponse = new FavorResponse();
        return ResponseEntity.ok(favorResponse);
    }
}