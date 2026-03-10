package com.vamsi.incident_management.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class CommentRequest {

    @NotNull(message = "Incident ID is required")
    private Long incidentId;

    @NotBlank(message = "Comment cannot be empty")
    private String comment;

    // Default constructor
    public CommentRequest() {
    }

    // Parameterized constructor
    public CommentRequest(Long incidentId, String comment) {
        this.incidentId = incidentId;
        this.comment = comment;
    }

    public Long getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}