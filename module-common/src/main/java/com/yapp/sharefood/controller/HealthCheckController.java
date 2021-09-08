package com.yapp.sharefood.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/healthcheck")
    public ResponseEntity<String> getHealthCheck() {
        
        return ResponseEntity.ok("health check");
    }
}
