package com.yapp.sharefood.auth.controller;

import com.yapp.sharefood.auth.dto.OAuthDto;
import com.yapp.sharefood.auth.dto.request.AuthCreationRequestDto;
import com.yapp.sharefood.auth.dto.request.AuthRequsetDto;
import com.yapp.sharefood.auth.service.AuthService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private static final String AUTH_TOKEN_HEADER = "Authorization";

    private final AuthService authService;

    @ApiOperation("login 회원 Token 발급")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] token header에 반환"),
            @ApiResponse(code = 400, message = "[error] access token과 제공되지 않는 oauth를 입력한 경우", response = HttpClientErrorException.BadRequest.class),
            @ApiResponse(code = 404, message = "[error] 회원 가입이 안되어 있을 경우", response = HttpClientErrorException.NotFound.class),
            @ApiResponse(code = 502, message = "[error] OAuth 연결 실패", response = HttpServerErrorException.BadGateway.class),
    })
    @PostMapping("/api/v1/auth")
    public ResponseEntity<Void> authenticate(@RequestBody @Valid AuthRequsetDto authRequsetDto, HttpServletResponse response) {
        OAuthDto oauthDto = authService.authenticate(authRequsetDto);
        response.setHeader(AUTH_TOKEN_HEADER, oauthDto.getToken());

        return ResponseEntity.ok().build();
    }

    @ApiOperation("회원 가입")
    @ApiResponses({
            @ApiResponse(code = 201, message = "[success] 회원 가입 후 token header에 반환", response = URI.class),
            @ApiResponse(code = 409, message = "[error] 이미 동일한 OAuth로 회원가입이 되어있는 경우", response = HttpClientErrorException.Conflict.class)
    })
    @PostMapping("/api/v1/auth/creation")
    public ResponseEntity<URI> signUp(@RequestBody @Valid AuthCreationRequestDto creationRequestDto, HttpServletResponse response) {
        OAuthDto oauthDto = authService.singUp(creationRequestDto);
        response.setHeader(AUTH_TOKEN_HEADER, oauthDto.getToken());

        URI userCreateUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(oauthDto.getUserId())
                .toUri();

        return ResponseEntity.created(userCreateUri).build();
    }
}
