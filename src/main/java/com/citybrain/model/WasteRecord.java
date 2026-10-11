package com.citybrain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "waste_records")
public class WasteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Location is required")
    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Column(name = "waste_type", length = 100)
    private String wasteType;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "collected_at", updatable = false)
    private LocalDateTime collectedAt;

    public WasteRecord() {}

    public WasteRecord(String location, String wasteType, Double quantity, String status) {
        this.location = location;
        this.wasteType = wasteType;
        this.quantity = quantity;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        if (this.collectedAt == null) {
            this.collectedAt = LocalDateTime.now();
        }
        if (this.status == null || this.status.isBlank()) {
            this.status = "Scheduled for Pickup";
        }
        if (this.wasteType == null || this.wasteType.isBlank()) {
            this.wasteType = "Municipal Solid Waste";
        }
        if (this.quantity == null) {
            this.quantity = 0.0;
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

    public String getWasteType() {
        return wasteType;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCollectedAt() {
        return collectedAt;
    }

    public void setCollectedAt(LocalDateTime collectedAt) {
        this.collectedAt = collectedAt;
    }
}
