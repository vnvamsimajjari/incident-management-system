package com.vamsi.incident_management.service;

import com.vamsi.incident_management.dto.IncidentRequest;
import com.vamsi.incident_management.dto.IncidentResponse;
import com.vamsi.incident_management.dto.IncidentAuditResponse;
import com.vamsi.incident_management.dto.DashboardSummaryResponse;
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

    // ================= FILTER + PAGINATION =================
    Page<IncidentResponse> getAllIncidents(
            Status status,
            Priority priority,
            Boolean breached,
            Pageable pageable);

    // ================= UPDATE INCIDENT =================
    IncidentResponse updateIncident(Long id, IncidentRequest request);

    // ================= ASSIGN INCIDENT =================
    IncidentResponse assignIncident(Long id, String assignedTo);

    // ================= UPDATE STATUS =================
    IncidentResponse updateIncidentStatus(Long id, Status status);

    // ================= INCIDENT AUDIT =================
    List<IncidentAuditResponse> getIncidentAudit(Long incidentId);

    // ================= DELETE INCIDENT =================
    void deleteIncident(Long id);

    // ================= RESTORE INCIDENT =================
    IncidentResponse restoreIncident(Long id);

    // ================= DASHBOARD =================

    // Get incidents that breached SLA
    List<IncidentResponse> getBreachedIncidents();

    // Get incidents assigned to logged-in user
    List<IncidentResponse> getMyIncidents();

    // Dashboard summary statistics
    DashboardSummaryResponse getDashboardSummary();

    // ================= COMMENTS MODULE =================

    // Add comment to incident
    IncidentComment addComment(Long incidentId, String message, String username);

    // Get comments for an incident
    List<IncidentComment> getComments(Long incidentId);

}