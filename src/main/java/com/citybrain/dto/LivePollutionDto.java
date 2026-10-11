package com.citybrain.dto;

import java.time.LocalDateTime;

public class LivePollutionDto {

    private String location;
    private Double latitude;
    private Double longitude;
    private Integer aqi;             // US AQI index (standard)
    private Integer europeanAqi;     // European AQI index
    private Double pm25;             // PM2.5 in µg/m³
    private Double pm10;             // PM10 in µg/m³
    private Double carbonMonoxide;   // CO in µg/m³
    private Double nitrogenDioxide;  // NO2 in µg/m³
    private Double sulphurDioxide;   // SO2 in µg/m³
    private Double ozone;            // O3 in µg/m³
    private String category;         // Good, Moderate, Unhealthy, etc.
    private String dominantPollutant;// PM2.5 or PM10
    private String observationTime;
    private LocalDateTime fetchedAt;
    private boolean isLive = true;
    private String source = "Open-Meteo Air Quality Live API";

    public LivePollutionDto() {
        this.fetchedAt = LocalDateTime.now();
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

    public Integer getAqi() {
        return aqi;
    }

    public void setAqi(Integer aqi) {
        this.aqi = aqi;
    }

    public Integer getEuropeanAqi() {
        return europeanAqi;
    }

    public void setEuropeanAqi(Integer europeanAqi) {
        this.europeanAqi = europeanAqi;
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

    public Double getCarbonMonoxide() {
        return carbonMonoxide;
    }

    public void setCarbonMonoxide(Double carbonMonoxide) {
        this.carbonMonoxide = carbonMonoxide;
    }

    public Double getNitrogenDioxide() {
        return nitrogenDioxide;
    }

    public void setNitrogenDioxide(Double nitrogenDioxide) {
        this.nitrogenDioxide = nitrogenDioxide;
    }

    public Double getSulphurDioxide() {
        return sulphurDioxide;
    }

    public void setSulphurDioxide(Double sulphurDioxide) {
        this.sulphurDioxide = sulphurDioxide;
    }

    public Double getOzone() {
        return ozone;
    }

    public void setOzone(Double ozone) {
        this.ozone = ozone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDominantPollutant() {
        return dominantPollutant;
    }

    public void setDominantPollutant(String dominantPollutant) {
        this.dominantPollutant = dominantPollutant;
    }

    public String getObservationTime() {
        return observationTime;
    }

    public void setObservationTime(String observationTime) {
        this.observationTime = observationTime;
    }

    public LocalDateTime getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(LocalDateTime fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public boolean isLive() {
        return isLive;
    }

    public boolean getIsLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
