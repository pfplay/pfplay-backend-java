package com.pfplaybackend.api.health.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok("success");
    }

    @GetMapping("/")
    public ResponseEntity<?> index() {
        return ResponseEntity.ok("welcome");
    }

    @GetMapping("/access")
    public ResponseEntity<?> access() {
        return ResponseEntity.ok("access");
    }

}
