package com.citybrain.repository;

import com.citybrain.model.PollutionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollutionRecordRepository extends JpaRepository<PollutionRecord, Long> {
    List<PollutionRecord> findByStatus(String status);
}
