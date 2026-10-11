package com.citybrain.service;

import com.citybrain.exception.ResourceNotFoundException;
import com.citybrain.model.RiskPrediction;
import com.citybrain.repository.RiskPredictionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RiskPredictionService {

    private final RiskPredictionRepository repository;

    public RiskPredictionService(RiskPredictionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<RiskPrediction> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public RiskPrediction getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RiskPrediction", "id", id));
    }

    public RiskPrediction create(RiskPrediction prediction) {
        prediction.setId(null);
        return repository.save(prediction);
    }

    public RiskPrediction update(Long id, RiskPrediction updatedData) {
        RiskPrediction existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RiskPrediction", "id", id));

        if (updatedData.getLocation() != null && !updatedData.getLocation().isBlank()) {
            existing.setLocation(updatedData.getLocation());
        }
        if (updatedData.getRiskType() != null) {
            existing.setRiskType(updatedData.getRiskType());
        }
        if (updatedData.getRiskLevel() != null) {
            existing.setRiskLevel(updatedData.getRiskLevel());
        }
        if (updatedData.getProbability() != null) {
            existing.setProbability(updatedData.getProbability());
        }
        if (updatedData.getDescription() != null) {
            existing.setDescription(updatedData.getDescription());
        }

        return repository.save(existing);
    }

    public void delete(Long id) {
        RiskPrediction existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RiskPrediction", "id", id));
        repository.delete(existing);
    }
}
