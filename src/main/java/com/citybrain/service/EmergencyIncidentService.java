package com.citybrain.service;

import com.citybrain.exception.ResourceNotFoundException;
import com.citybrain.model.EmergencyIncident;
import com.citybrain.repository.EmergencyIncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmergencyIncidentService {

    private final EmergencyIncidentRepository repository;

    public EmergencyIncidentService(EmergencyIncidentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EmergencyIncident> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public EmergencyIncident getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyIncident", "id", id));
    }

    public EmergencyIncident create(EmergencyIncident incident) {
        incident.setId(null);
        return repository.save(incident);
    }

    public EmergencyIncident update(Long id, EmergencyIncident updatedData) {
        EmergencyIncident existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyIncident", "id", id));

        if (updatedData.getType() != null && !updatedData.getType().isBlank()) {
            existing.setType(updatedData.getType());
        }
        if (updatedData.getLocation() != null && !updatedData.getLocation().isBlank()) {
            existing.setLocation(updatedData.getLocation());
        }
        if (updatedData.getSeverity() != null) {
            existing.setSeverity(updatedData.getSeverity());
        }
        if (updatedData.getDescription() != null) {
            existing.setDescription(updatedData.getDescription());
        }
        if (updatedData.getStatus() != null) {
            existing.setStatus(updatedData.getStatus());
        }

        return repository.save(existing);
    }

    public void delete(Long id) {
        EmergencyIncident existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyIncident", "id", id));
        repository.delete(existing);
    }
}
