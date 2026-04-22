package com.vamsi.incident_management.service;

import com.vamsi.incident_management.dto.IncidentAuditResponse;
import com.vamsi.incident_management.dto.IncidentRequest;
import com.vamsi.incident_management.dto.IncidentResponse;
import com.vamsi.incident_management.entity.Priority;
import com.vamsi.incident_management.entity.Status;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IncidentService {

    // ================= CREATE =================
    IncidentResponse createIncident(IncidentRequest request);

    // ================= GET =================
    IncidentResponse getIncidentById(Long id);

    // ================= FILTER + PAGINATION =================
    Page<IncidentResponse> getAllIncidents(
            Status status,
            Priority priority,
            Boolean breached,
            Pageable pageable
    );

    // ================= UPDATE =================
    IncidentResponse updateIncident(Long id, IncidentRequest request);

    // ================= STATUS =================
    IncidentResponse updateIncidentStatus(Long id, Status status);

    // ================= AUDIT =================
    List<IncidentAuditResponse> getAuditHistory(Long id);
}