package com.citybrain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "traffic")
public class Traffic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;
    private Integer congestion;
    private Integer vehicles;
    private String status;

    @Column(name = "recorded_at")
    private java.time.LocalDateTime recordedAt;

    public Traffic() {
    }

    public Long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public Integer getCongestion() {
        return congestion;
    }

    public Integer getVehicles() {
        return vehicles;
    }

    public String getStatus() {
        return status;
    }

    public java.time.LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCongestion(Integer congestion) {
        this.congestion = congestion;
    }

    public void setVehicles(Integer vehicles) {
        this.vehicles = vehicles;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRecordedAt(java.time.LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}