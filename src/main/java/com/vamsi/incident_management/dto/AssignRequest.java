package com.vamsi.incident_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignRequest {

    @NotBlank(message = "Assigned engineer username is mandatory")
    @Size(max = 100, message = "Username must not exceed 100 characters")
    private String assignedEngineerUsername;
}