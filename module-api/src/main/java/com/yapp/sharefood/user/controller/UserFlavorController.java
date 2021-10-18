package com.yapp.sharefood.user.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.favor.dto.FavorDto;
import com.yapp.sharefood.favor.dto.request.MeFavorRequest;
import com.yapp.sharefood.favor.dto.response.FavorResponse;
import com.yapp.sharefood.user.domain.User;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URI;
import java.util.List;

@RestController
public class UserFlavorController {
    @PostMapping("/api/v1/users/{userId}/flavor/me")
    @ApiOperation("[auth] 사용자가 입력한 입맛 정보 저장")
    @ApiResponses({
            @ApiResponse(code = 201, message = "[success] 입 맛 정보 저장 완료"),
            @ApiResponse(code = 400, message = "[error] 입맛 정보를 입력하지 않은 경우와 입맛 정보가 없는 경우", response = HttpClientErrorException.BadRequest.class)
    })
    public ResponseEntity<URI> createUserFavor(@RequestBody MeFavorRequest meFavorRequest, @PathVariable("userId") Long userId,
                                               @ApiIgnore @AuthUser User user) {
        // 다중 추가
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/v1/users/{userId}/flavors/me")
    @ApiOperation("[auth] 사용자가 입력한 입맛 정보 저장")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 입 맛 정보 반환", response = FavorResponse.class)
    })
    public ResponseEntity<FavorResponse> findUserFavors(@ApiIgnore @AuthUser User user, @PathVariable("userId") Long userId) {
        FavorResponse favorResponse = new FavorResponse();
        favorResponse.setFlavors(List.of(new FavorDto(1L, "매운맛"), new FavorDto(2L, "단 맛")));

        return ResponseEntity.ok(favorResponse);
    }
}
