package com.citybrain.controller;

import com.citybrain.model.PollutionRecord;
import com.citybrain.service.PollutionRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pollution")
public class PollutionRecordController {

    private final PollutionRecordService service;

    public PollutionRecordController(PollutionRecordService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PollutionRecord>> getAllPollutionRecords() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PollutionRecord> getPollutionRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<PollutionRecord> createPollutionRecord(@Valid @RequestBody PollutionRecord record) {
        PollutionRecord created = service.create(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PollutionRecord> updatePollutionRecord(
            @PathVariable Long id,
            @RequestBody PollutionRecord record) {
        PollutionRecord updated = service.update(id, record);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePollutionRecord(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
