package com.yapp.sharefood.auth.controller;

import com.yapp.sharefood.auth.dto.request.AuthRequsetDto;
import com.yapp.sharefood.auth.service.AuthService;
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

    private final AuthService authService;

    @ApiOperation("회원 가입 API")
    @PostMapping("/api/v1/auth")
    public ResponseEntity<?> signUpClient(@RequestBody @Valid AuthRequsetDto authRequsetDto, HttpServletResponse response) {

        return ResponseEntity.ok(authRequsetDto);
    }
}
