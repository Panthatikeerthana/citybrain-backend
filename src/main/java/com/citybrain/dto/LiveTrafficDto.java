package com.citybrain.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LiveTrafficDto {

    private boolean configured;
    private String provider = "TomTom Traffic Flow API";
    private String status;           // "ACTIVE", "CONFIG_REQUIRED", "AUTH_ERROR", "RATE_LIMITED", "TIMEOUT", "ERROR"
    private String message;
    private List<TrafficFlowItem> items = new ArrayList<>();
    private LocalDateTime fetchedAt;
    private boolean isLive;

    public LiveTrafficDto() {
        this.fetchedAt = LocalDateTime.now();
    }

    public static LiveTrafficDto configRequired() {
        LiveTrafficDto dto = new LiveTrafficDto();
        dto.setConfigured(false);
        dto.setLive(false);
        dto.setStatus("CONFIG_REQUIRED");
        dto.setMessage("Live traffic unavailable — TomTom API key required");
        return dto;
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TrafficFlowItem> getItems() {
        return items;
    }

    public void setItems(List<TrafficFlowItem> items) {
        this.items = items;
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

    public Integer getAverageCongestion() {
        if (items == null || items.isEmpty()) return null;
        int sum = 0;
        for (TrafficFlowItem item : items) {
            sum += (item.getCongestionLevel() != null ? item.getCongestionLevel() : 0);
        }
        return Math.round((float) sum / items.size());
    }

    public Integer getAverageSpeed() {
        if (items == null || items.isEmpty()) return null;
        int sum = 0;
        for (TrafficFlowItem item : items) {
            sum += (item.getCurrentSpeed() != null ? item.getCurrentSpeed() : 0);
        }
        return Math.round((float) sum / items.size());
    }

    public Integer getTotalCorridors() {
        return items != null ? items.size() : 0;
    }
}
