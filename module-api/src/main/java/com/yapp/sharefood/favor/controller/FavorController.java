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

@RestController
public class FavorController {

    @GetMapping("/api/v1/favors")
    @ApiOperation("입맛 정보 전체 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 입 맛 정보 반환", response = FavorResponse.class)
    })
    public ResponseEntity<FavorResponse> findAllTypeFavors() {
        FavorResponse favorResponse = new FavorResponse();
        return ResponseEntity.ok(favorResponse);
    }

    @PostMapping("/api/v1/favors/me")
    @ApiOperation("[auth] 사용자가 입력한 입맛 정보 저장")
    @ApiResponses({
            @ApiResponse(code = 201, message = "[success] 입 맛 정보 저장 완료"),
            @ApiResponse(code = 400, message = "[error] 입맛 정보를 입력하지 않은 경우와 입맛 정보가 없는 경우", response = HttpClientErrorException.BadRequest.class)
    })
    public ResponseEntity<Void> createUserFavor(@RequestBody MeFavorRequest meFavorRequest,
                                                @AuthUser User user) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/v1/favors/me")
    @ApiOperation("[auth] 사용자가 입력한 입맛 정보 저장")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 입 맛 정보 반환", response = FavorResponse.class)
    })
    public ResponseEntity<FavorResponse> findUserFavors(@AuthUser User user) {
        FavorResponse favorResponse = new FavorResponse();
        return ResponseEntity.ok(favorResponse);
    }
}