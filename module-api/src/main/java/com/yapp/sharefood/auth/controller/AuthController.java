package com.yapp.sharefood.auth.controller;

import com.yapp.sharefood.auth.dto.OAuthDto;
import com.yapp.sharefood.auth.dto.request.AuthCreationRequestDto;
import com.yapp.sharefood.auth.dto.request.AuthRequestDto;
import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.auth.service.AuthService;
import com.yapp.sharefood.auth.utils.AuthUtils;
import com.yapp.sharefood.user.domain.User;
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
    private final AuthService authService;

    @PostMapping("/api/v1/auth")
    public ResponseEntity<Void> authenticateLegacy(@RequestBody @Valid AuthRequestDto authRequestDto, HttpServletResponse response) {
        return authenticatedUser(authRequestDto, response);
    }

    @PostMapping("/api/v1/users/auth")
    public ResponseEntity<Void> authenticate(@RequestBody @Valid AuthRequestDto authRequestDto, HttpServletResponse response) {
        return authenticatedUser(authRequestDto, response);
    }

    private ResponseEntity<Void> authenticatedUser(AuthRequestDto authRequestDto, HttpServletResponse response) {
        OAuthDto oauthDto = authService.authenticate(authRequestDto);
        AuthUtils.setTokenInHeader(response, oauthDto.getToken());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/auth/creation")
    public ResponseEntity<URI> signUpLegacy(@RequestBody @Valid AuthCreationRequestDto creationRequestDto, HttpServletResponse response) {
        OAuthDto oauthDto = authService.signUp(creationRequestDto);
        AuthUtils.setTokenInHeader(response, oauthDto.getToken());

        URI userCreateUri = ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/api/v1/users/{id}")
                .buildAndExpand(oauthDto.getUserId())
                .toUri();

        return ResponseEntity.created(userCreateUri).build();
    }

    @PostMapping("/api/v1/users/creation")
    public ResponseEntity<URI> signUp(@RequestBody @Valid AuthCreationRequestDto creationRequestDto, HttpServletResponse response) {
        OAuthDto oauthDto = authService.signUp(creationRequestDto);
        AuthUtils.setTokenInHeader(response, oauthDto.getToken());

        URI userCreateUri = ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/api/v1/users/{id}")
                .buildAndExpand(oauthDto.getUserId())
                .toUri();

        return ResponseEntity.created(userCreateUri).build();
    }

    @PostMapping("/api/v1/users/auth/token")
    public ResponseEntity<Void> refreshToken(@AuthUser User user, HttpServletResponse response) {
        AuthUtils.setTokenInHeader(response, authService.refreshToken(user));
        return ResponseEntity.ok().build();
    }
}
