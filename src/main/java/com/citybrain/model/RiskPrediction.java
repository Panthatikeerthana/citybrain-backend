package com.citybrain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_predictions")
public class RiskPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Location is required")
    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Column(name = "risk_type", length = 100)
    private String riskType;

    @Column(name = "risk_level", length = 50)
    private String riskLevel;

    @Column(name = "probability")
    private Double probability;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "predicted_at", updatable = false)
    private LocalDateTime predictedAt;

    public RiskPrediction() {}

    public RiskPrediction(String location, String riskType, String riskLevel, Double probability, String description) {
        this.location = location;
        this.riskType = riskType;
        this.riskLevel = riskLevel;
        this.probability = probability;
        this.description = description;
    }

    @PrePersist
    protected void onCreate() {
        if (this.predictedAt == null) {
            this.predictedAt = LocalDateTime.now();
        }
        if (this.riskLevel == null || this.riskLevel.isBlank()) {
            this.riskLevel = "Medium";
        }
        if (this.probability == null) {
            this.probability = 0.5;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getPredictedAt() {
        return predictedAt;
    }

    public void setPredictedAt(LocalDateTime predictedAt) {
        this.predictedAt = predictedAt;
    }
}
