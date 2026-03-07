package com.vamsi.incident_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignRequest {

    @NotBlank(message = "assignedTo cannot be empty")
    private String assignedTo;
}