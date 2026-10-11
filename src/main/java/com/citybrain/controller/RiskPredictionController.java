package com.citybrain.controller;

import com.citybrain.model.RiskPrediction;
import com.citybrain.service.RiskPredictionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risks")
public class RiskPredictionController {

    private final RiskPredictionService service;

    public RiskPredictionController(RiskPredictionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RiskPrediction>> getAllRisks() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RiskPrediction> getRiskById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<RiskPrediction> createRisk(@Valid @RequestBody RiskPrediction risk) {
        RiskPrediction created = service.create(risk);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RiskPrediction> updateRisk(
            @PathVariable Long id,
            @RequestBody RiskPrediction risk) {
        RiskPrediction updated = service.update(id, risk);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRisk(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
