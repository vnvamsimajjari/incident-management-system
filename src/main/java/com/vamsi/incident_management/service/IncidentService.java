package com.vamsi.incident_management.service;

import com.vamsi.incident_management.dto.DashboardSummaryResponse;
import com.vamsi.incident_management.dto.IncidentAuditResponse;
import com.vamsi.incident_management.dto.IncidentRequest;
import com.vamsi.incident_management.dto.IncidentResponse;
import com.vamsi.incident_management.entity.IncidentComment;
import com.vamsi.incident_management.entity.Priority;
import com.vamsi.incident_management.entity.Status;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IncidentService {


    // ================= CREATE INCIDENT =================
    IncidentResponse createIncident(IncidentRequest request);

    // ================= GET INCIDENT =================
    IncidentResponse getIncident(Long id);

    // ================= LIST INCIDENTS (FILTER + PAGINATION) =================
    Page<IncidentResponse> getAllIncidents(
            Status status,
            Priority priority,
            Boolean breached,
            Pageable pageable
    );

    // ================= UPDATE INCIDENT =================
    IncidentResponse updateIncident(Long id, IncidentRequest request);

    // ================= ASSIGN INCIDENT =================
    IncidentResponse assignIncident(Long id, String assignedTo);

    // ================= STATUS MANAGEMENT =================
    IncidentResponse updateIncidentStatus(Long id, Status status);

    // ================= INCIDENT LIFECYCLE =================
    IncidentResponse resolveIncident(Long id);

    IncidentResponse closeIncident(Long id);

    // ================= INCIDENT AUDIT =================
    List<IncidentAuditResponse> getIncidentAudit(Long incidentId);

    // ================= COMMENTS =================
    IncidentComment addComment(Long incidentId, String message, String username);

    List<IncidentComment> getComments(Long incidentId);

    // ================= DELETE / RESTORE =================
    void deleteIncident(Long id);

    IncidentResponse restoreIncident(Long id);

    // ================= DASHBOARD =================
    List<IncidentResponse> getBreachedIncidents();

    List<IncidentResponse> getMyIncidents();

    DashboardSummaryResponse getDashboardSummary();


}
