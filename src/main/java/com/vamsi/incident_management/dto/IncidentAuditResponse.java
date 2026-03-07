package com.vamsi.incident_management.dto;

import com.vamsi.incident_management.entity.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class IncidentAuditResponse {

    private Status oldStatus;
    private Status newStatus;
    private LocalDateTime changedAt;
}