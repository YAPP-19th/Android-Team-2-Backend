package com.yapp.sharefood.auth.controller;

import com.yapp.sharefood.auth.dto.OAuthDto;
import com.yapp.sharefood.auth.dto.request.AuthCreationRequestDto;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private static final String AUTH_TOKEN_HEADER = "Authorization";

    private final AuthService authService;

    @ApiOperation("login 회원 Token 발급")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accessToken", value = "OAuth Access Token", required = true),
            @ApiImplicitParam(name = "authType", value = "KAKAO", required = true)
    })
    @PostMapping("/api/v1/auth")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthRequsetDto authRequsetDto, HttpServletResponse response) {
        OAuthDto oauthDto = authService.authenticate(authRequsetDto);
        response.setHeader(AUTH_TOKEN_HEADER, oauthDto.getToken());

        return ResponseEntity.ok().build();
    }

    @ApiOperation("회원 가입")
    @PostMapping("/api/v1/auth/creation")
    public ResponseEntity<?> signUp(@RequestBody @Valid AuthCreationRequestDto creationRequestDto, HttpServletResponse response) {
        return null;
    }
}
