package com.vamsi.incident_management.dto;

import com.vamsi.incident_management.entity.Priority;
import com.vamsi.incident_management.entity.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class IncidentResponse {

    private Long id;
    private String title;
    private String description;

    private Priority priority;
    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ADD THIS FIELD
    private String assignedTo;
}