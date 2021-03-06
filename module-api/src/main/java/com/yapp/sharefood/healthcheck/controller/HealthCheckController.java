package com.yapp.sharefood.healthcheck.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {
    private final Environment env;

    @GetMapping("/healthcheck")
    public ResponseEntity<String> getHealthCheck() {

        return ResponseEntity.ok("health check," + " Port(server.port) = " + env.getProperty("server.port"));
    }
}
