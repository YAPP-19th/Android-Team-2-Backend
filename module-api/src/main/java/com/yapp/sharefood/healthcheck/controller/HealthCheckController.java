package com.yapp.sharefood.healthcheck.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @ApiOperation("Application 생존 검사")
    @GetMapping("/healthcheck")
    public ResponseEntity<String> getHealthCheck() {

        return ResponseEntity.ok("health check");
    }
}
