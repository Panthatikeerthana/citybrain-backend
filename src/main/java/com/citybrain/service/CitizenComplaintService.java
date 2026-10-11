package com.citybrain.service;

import com.citybrain.exception.ResourceNotFoundException;
import com.citybrain.model.CitizenComplaint;
import com.citybrain.repository.CitizenComplaintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CitizenComplaintService {

    private final CitizenComplaintRepository repository;

    public CitizenComplaintService(CitizenComplaintRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CitizenComplaint> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public CitizenComplaint getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CitizenComplaint", "id", id));
    }

    public CitizenComplaint create(CitizenComplaint complaint) {
        complaint.setId(null); // Ensure creation of a new entity
        return repository.save(complaint);
    }

    public CitizenComplaint update(Long id, CitizenComplaint updatedData) {
        CitizenComplaint existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CitizenComplaint", "id", id));

        if (updatedData.getCitizenName() != null && !updatedData.getCitizenName().isBlank()) {
            existing.setCitizenName(updatedData.getCitizenName());
        }
        if (updatedData.getCategory() != null && !updatedData.getCategory().isBlank()) {
            existing.setCategory(updatedData.getCategory());
        }
        if (updatedData.getDescription() != null && !updatedData.getDescription().isBlank()) {
            existing.setDescription(updatedData.getDescription());
        }
        if (updatedData.getLocation() != null && !updatedData.getLocation().isBlank()) {
            existing.setLocation(updatedData.getLocation());
        }
        if (updatedData.getPriority() != null) {
            existing.setPriority(updatedData.getPriority());
        }
        if (updatedData.getStatus() != null) {
            existing.setStatus(updatedData.getStatus());
        }

        return repository.save(existing);
    }

    public void delete(Long id) {
        CitizenComplaint existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CitizenComplaint", "id", id));
        repository.delete(existing);
    }
}
