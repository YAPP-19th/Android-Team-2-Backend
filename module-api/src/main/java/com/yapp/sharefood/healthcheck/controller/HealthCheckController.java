package com.yapp.sharefood.healthcheck.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {
    private final Environment env;

    @ApiOperation("Application 생존 검사")
    @GetMapping("/healthcheck")
    public ResponseEntity<String> getHealthCheck() {

        return ResponseEntity.ok("health check," + " Port(server.port) = " + env.getProperty("server.port"));
    }
}
