package com.citybrain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "water_issues")
public class WaterIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Location is required")
    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Column(name = "issue_type", length = 100)
    private String issueType;

    @Column(name = "severity", length = 50)
    private String severity;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "reported_at", updatable = false)
    private LocalDateTime reportedAt;

    public WaterIssue() {}

    public WaterIssue(String location, String issueType, String severity, String description, String status) {
        this.location = location;
        this.issueType = issueType;
        this.severity = severity;
        this.description = description;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        if (this.reportedAt == null) {
            this.reportedAt = LocalDateTime.now();
        }
        if (this.status == null || this.status.isBlank()) {
            this.status = "Reported";
        }
        if (this.severity == null || this.severity.isBlank()) {
            this.severity = "Medium";
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

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }
}
