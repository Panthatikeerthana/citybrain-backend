package com.citybrain.service;

import com.citybrain.exception.ResourceNotFoundException;
import com.citybrain.model.WasteRecord;
import com.citybrain.repository.WasteRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WasteRecordService {

    private final WasteRecordRepository repository;

    public WasteRecordService(WasteRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<WasteRecord> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public WasteRecord getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WasteRecord", "id", id));
    }

    public WasteRecord create(WasteRecord record) {
        record.setId(null);
        return repository.save(record);
    }

    public WasteRecord update(Long id, WasteRecord updatedData) {
        WasteRecord existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WasteRecord", "id", id));

        if (updatedData.getLocation() != null && !updatedData.getLocation().isBlank()) {
            existing.setLocation(updatedData.getLocation());
        }
        if (updatedData.getWasteType() != null) {
            existing.setWasteType(updatedData.getWasteType());
        }
        if (updatedData.getQuantity() != null) {
            existing.setQuantity(updatedData.getQuantity());
        }
        if (updatedData.getStatus() != null) {
            existing.setStatus(updatedData.getStatus());
        }

        return repository.save(existing);
    }

    public void delete(Long id) {
        WasteRecord existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WasteRecord", "id", id));
        repository.delete(existing);
    }
}
