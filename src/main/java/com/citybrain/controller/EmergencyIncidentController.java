package com.citybrain.controller;

import com.citybrain.model.EmergencyIncident;
import com.citybrain.service.EmergencyIncidentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergencies")
public class EmergencyIncidentController {

    private final EmergencyIncidentService service;

    public EmergencyIncidentController(EmergencyIncidentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EmergencyIncident>> getAllEmergencies() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmergencyIncident> getEmergencyById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<EmergencyIncident> createEmergency(@Valid @RequestBody EmergencyIncident incident) {
        EmergencyIncident created = service.create(incident);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmergencyIncident> updateEmergency(
            @PathVariable Long id,
            @RequestBody EmergencyIncident incident) {
        EmergencyIncident updated = service.update(id, incident);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmergency(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
