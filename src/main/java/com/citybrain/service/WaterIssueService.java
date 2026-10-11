package com.citybrain.service;

import com.citybrain.exception.ResourceNotFoundException;
import com.citybrain.model.WaterIssue;
import com.citybrain.repository.WaterIssueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WaterIssueService {

    private final WaterIssueRepository repository;

    public WaterIssueService(WaterIssueRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<WaterIssue> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public WaterIssue getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WaterIssue", "id", id));
    }

    public WaterIssue create(WaterIssue issue) {
        issue.setId(null);
        return repository.save(issue);
    }

    public WaterIssue update(Long id, WaterIssue updatedData) {
        WaterIssue existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WaterIssue", "id", id));

        if (updatedData.getLocation() != null && !updatedData.getLocation().isBlank()) {
            existing.setLocation(updatedData.getLocation());
        }
        if (updatedData.getIssueType() != null) {
            existing.setIssueType(updatedData.getIssueType());
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
        WaterIssue existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WaterIssue", "id", id));
        repository.delete(existing);
    }
}
