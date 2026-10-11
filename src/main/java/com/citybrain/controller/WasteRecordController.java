package com.citybrain.controller;

import com.citybrain.model.WasteRecord;
import com.citybrain.service.WasteRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waste")
public class WasteRecordController {

    private final WasteRecordService service;

    public WasteRecordController(WasteRecordService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<WasteRecord>> getAllWasteRecords() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WasteRecord> getWasteRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<WasteRecord> createWasteRecord(@Valid @RequestBody WasteRecord record) {
        WasteRecord created = service.create(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WasteRecord> updateWasteRecord(
            @PathVariable Long id,
            @RequestBody WasteRecord record) {
        WasteRecord updated = service.update(id, record);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWasteRecord(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
