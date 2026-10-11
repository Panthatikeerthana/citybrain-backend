package com.citybrain.controller;

import com.citybrain.model.CitizenComplaint;
import com.citybrain.service.CitizenComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class CitizenComplaintController {

    private final CitizenComplaintService service;

    public CitizenComplaintController(CitizenComplaintService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CitizenComplaint>> getAllComplaints() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitizenComplaint> getComplaintById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<CitizenComplaint> createComplaint(@Valid @RequestBody CitizenComplaint complaint) {
        CitizenComplaint created = service.create(complaint);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitizenComplaint> updateComplaint(
            @PathVariable Long id,
            @RequestBody CitizenComplaint complaint) {
        CitizenComplaint updated = service.update(id, complaint);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
