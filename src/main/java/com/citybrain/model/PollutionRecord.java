package com.citybrain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "pollution_records")
public class PollutionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Location is required")
    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Column(name = "aqi")
    private Integer aqi;

    @Column(name = "pm25")
    private Double pm25;

    @Column(name = "pm10")
    private Double pm10;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "recorded_at", updatable = false)
    private LocalDateTime recordedAt;

    public PollutionRecord() {}

    public PollutionRecord(String location, Integer aqi, Double pm25, Double pm10, String status) {
        this.location = location;
        this.aqi = aqi;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        if (this.recordedAt == null) {
            this.recordedAt = LocalDateTime.now();
        }
        if (this.aqi == null) {
            this.aqi = 50;
        }
        if (this.status == null || this.status.isBlank()) {
            if (this.aqi > 200) this.status = "Very Unhealthy";
            else if (this.aqi > 150) this.status = "Unhealthy";
            else if (this.aqi > 100) this.status = "Moderate";
            else this.status = "Good";
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

    public Integer getAqi() {
        return aqi;
    }

    public void setAqi(Integer aqi) {
        this.aqi = aqi;
    }

    public Double getPm25() {
        return pm25;
    }

    public void setPm25(Double pm25) {
        this.pm25 = pm25;
    }

    public Double getPm10() {
        return pm10;
    }

    public void setPm10(Double pm10) {
        this.pm10 = pm10;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
