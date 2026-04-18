package com.vamsi.incident_management.dto;

import com.vamsi.incident_management.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IncidentRequest {

    @NotBlank(message = "Title is mandatory")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Priority is mandatory")
    private Priority priority;

    @NotBlank(message = "Assigned engineer is mandatory")
    private String assignedTo;

  //  @NotNull(message = "SLA minutes is mandatory")
    private Integer slaMinutes;


}