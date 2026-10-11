package com.citybrain.controller;

import com.citybrain.model.WaterIssue;
import com.citybrain.service.WaterIssueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/water")
public class WaterIssueController {

    private final WaterIssueService service;

    public WaterIssueController(WaterIssueService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<WaterIssue>> getAllWaterIssues() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaterIssue> getWaterIssueById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<WaterIssue> createWaterIssue(@Valid @RequestBody WaterIssue issue) {
        WaterIssue created = service.create(issue);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WaterIssue> updateWaterIssue(
            @PathVariable Long id,
            @RequestBody WaterIssue issue) {
        WaterIssue updated = service.update(id, issue);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaterIssue(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
