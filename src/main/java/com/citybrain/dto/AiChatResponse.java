package com.citybrain.dto;

import java.time.LocalDateTime;

public class AiChatResponse {

    private String response;
    private String status;
    private LocalDateTime timestamp;

    public AiChatResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public AiChatResponse(String response) {
        this.response = response;
        this.status = "SUCCESS";
        this.timestamp = LocalDateTime.now();
    }

    public AiChatResponse(String response, String status) {
        this.response = response;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
