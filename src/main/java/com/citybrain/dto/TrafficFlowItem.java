package com.citybrain.dto;

public class TrafficFlowItem {

    private String location;
    private Double latitude;
    private Double longitude;
    private Integer congestionLevel; // 0 - 100%
    private Integer currentSpeed;    // km/h
    private Integer freeFlowSpeed;   // km/h
    private String status;           // Smooth, Moderate, Heavy, Severe
    private Double confidence;
    private String observationTime;

    public TrafficFlowItem() {
    }

    public TrafficFlowItem(String location, Double latitude, Double longitude, Integer congestionLevel,
                           Integer currentSpeed, Integer freeFlowSpeed, String status, Double confidence,
                           String observationTime) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.congestionLevel = congestionLevel;
        this.currentSpeed = currentSpeed;
        this.freeFlowSpeed = freeFlowSpeed;
        this.status = status;
        this.confidence = confidence;
        this.observationTime = observationTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getCongestionLevel() {
        return congestionLevel;
    }

    public void setCongestionLevel(Integer congestionLevel) {
        this.congestionLevel = congestionLevel;
    }

    public Integer getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(Integer currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public Integer getFreeFlowSpeed() {
        return freeFlowSpeed;
    }

    public void setFreeFlowSpeed(Integer freeFlowSpeed) {
        this.freeFlowSpeed = freeFlowSpeed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getObservationTime() {
        return observationTime;
    }

    public void setObservationTime(String observationTime) {
        this.observationTime = observationTime;
    }
}
