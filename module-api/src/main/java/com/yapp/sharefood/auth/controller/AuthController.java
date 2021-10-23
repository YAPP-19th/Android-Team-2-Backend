package com.yapp.sharefood.auth.controller;

import com.yapp.sharefood.auth.dto.OAuthDto;
import com.yapp.sharefood.auth.dto.request.AuthCreationRequestDto;
import com.yapp.sharefood.auth.dto.request.AuthRequestDto;
import com.yapp.sharefood.auth.service.AuthService;
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

    @PostMapping("/api/v1/auth")
    public ResponseEntity<Void> authenticate(@RequestBody @Valid AuthRequestDto authRequestDto, HttpServletResponse response) {
        OAuthDto oauthDto = authService.authenticate(authRequestDto);
        response.setHeader(AUTH_TOKEN_HEADER, oauthDto.getToken());

        return ResponseEntity.ok().build();
    }

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
