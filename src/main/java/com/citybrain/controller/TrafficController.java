package com.citybrain.controller;

import com.citybrain.exception.ResourceNotFoundException;
import com.citybrain.model.Traffic;
import com.citybrain.repository.TrafficRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/traffic")
public class TrafficController {

    private final TrafficRepository trafficRepository;

    public TrafficController(TrafficRepository trafficRepository) {
        this.trafficRepository = trafficRepository;
    }

    @GetMapping
    public List<Traffic> getAllTraffic() {
        return trafficRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Traffic> getTrafficById(@PathVariable Long id) {
        Traffic traffic = trafficRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Traffic", "id", id));
        return ResponseEntity.ok(traffic);
    }

    @PostMapping
    public ResponseEntity<Traffic> createTraffic(@RequestBody Traffic traffic) {
        traffic.setId(null);
        if (traffic.getRecordedAt() == null) {
            traffic.setRecordedAt(LocalDateTime.now());
        }
        Traffic saved = trafficRepository.save(traffic);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Traffic> updateTraffic(@PathVariable Long id, @RequestBody Traffic updated) {
        Traffic existing = trafficRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Traffic", "id", id));

        if (updated.getLocation() != null) existing.setLocation(updated.getLocation());
        if (updated.getCongestion() != null) existing.setCongestion(updated.getCongestion());
        if (updated.getVehicles() != null) existing.setVehicles(updated.getVehicles());
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());

        return ResponseEntity.ok(trafficRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraffic(@PathVariable Long id) {
        Traffic existing = trafficRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Traffic", "id", id));
        trafficRepository.delete(existing);
        return ResponseEntity.noContent().build();
    }
}
