package com.citybrain.repository;

import com.citybrain.model.RiskPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskPredictionRepository extends JpaRepository<RiskPrediction, Long> {
    List<RiskPrediction> findByRiskLevel(String riskLevel);
    List<RiskPrediction> findByRiskType(String riskType);
}
