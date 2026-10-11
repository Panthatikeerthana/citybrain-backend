package com.citybrain.service;

import com.citybrain.exception.ResourceNotFoundException;
import com.citybrain.model.PollutionRecord;
import com.citybrain.repository.PollutionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PollutionRecordService {

    private final PollutionRecordRepository repository;

    public PollutionRecordService(PollutionRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<PollutionRecord> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public PollutionRecord getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PollutionRecord", "id", id));
    }

    public PollutionRecord create(PollutionRecord record) {
        record.setId(null);
        return repository.save(record);
    }

    public PollutionRecord update(Long id, PollutionRecord updatedData) {
        PollutionRecord existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PollutionRecord", "id", id));

        if (updatedData.getLocation() != null && !updatedData.getLocation().isBlank()) {
            existing.setLocation(updatedData.getLocation());
        }
        if (updatedData.getAqi() != null) {
            existing.setAqi(updatedData.getAqi());
        }
        if (updatedData.getPm25() != null) {
            existing.setPm25(updatedData.getPm25());
        }
        if (updatedData.getPm10() != null) {
            existing.setPm10(updatedData.getPm10());
        }
        if (updatedData.getStatus() != null) {
            existing.setStatus(updatedData.getStatus());
        }

        return repository.save(existing);
    }

    public void delete(Long id) {
        PollutionRecord existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PollutionRecord", "id", id));
        repository.delete(existing);
    }
}
