package com.vamsi.incident_management.dto;

import com.vamsi.incident_management.entity.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IncidentStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private Status status;
}