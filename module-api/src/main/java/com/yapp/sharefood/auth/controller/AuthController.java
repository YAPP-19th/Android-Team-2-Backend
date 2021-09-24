package com.yapp.sharefood.auth.controller;

import com.yapp.sharefood.auth.dto.OAuthDto;
import com.yapp.sharefood.auth.dto.request.AuthRequsetDto;
import com.yapp.sharefood.auth.service.AuthService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private static final String AUTH_TOKEN_HEADER = "Authorization";

    private final AuthService authService;

    @ApiOperation("회원 가입 API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accessToken", value = "OAuth Access Token", required = true),
            @ApiImplicitParam(name = "authType", value = "KAKAO", required = true)
    })
    @PostMapping("/api/v1/auth")
    public ResponseEntity<URI> signUpClient(@RequestBody @Valid AuthRequsetDto authRequsetDto, HttpServletResponse response) {
        OAuthDto oauthDto = authService.authenticate(authRequsetDto);
        response.setHeader(AUTH_TOKEN_HEADER, oauthDto.getToken());

        URI authUserUri = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(oauthDto.getUserId())
                .toUri();

        return ResponseEntity.created(authUserUri).build();
    }
}
