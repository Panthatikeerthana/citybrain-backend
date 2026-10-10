package com.citybrain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather")
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;
    private Double temperature;
    private Integer humidity;
    private Double rainfall;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    public Weather() {
    }

    public Long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public Double getRainfall() {
        return rainfall;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public void setRainfall(Double rainfall) {
        this.rainfall = rainfall;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}