package com.citybrain.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "UP");
        body.put("service", "City Brain Backend");
        body.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(body);
    }
}
