package com.vamsi.incident_management.service;

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

    // ================= CREATE =================
    IncidentResponse createIncident(IncidentRequest request);

    // ================= GET =================
    IncidentResponse getIncident(Long id);

    // ================= FILTER + PAGINATION =================
    Page<IncidentResponse> getAllIncidents(
            Status status,
            Priority priority,
            Boolean breached,
            Pageable pageable
    );

    // ================= UPDATE =================
    IncidentResponse updateIncident(Long id, IncidentRequest request);

    // 🔥 IMPORTANT: This is what your UI uses
    IncidentResponse updateIncidentStatus(Long id, Status status);

    // ================= ASSIGN =================
    IncidentResponse assignIncident(Long id, String assignedEngineerUsername);

    // ================= LIFECYCLE =================
    IncidentResponse resolveIncident(Long id);
    IncidentResponse closeIncident(Long id);

    // ================= COMMENTS =================
    IncidentComment addComment(Long incidentId, String message, String username);
    List<IncidentComment> getComments(Long incidentId);

    // ================= DASHBOARD LIST =================
    List<IncidentResponse> getBreachedIncidents();
    List<IncidentResponse> getMyIncidents();

    // ================= DASHBOARD COUNT =================
    int getTotalIncidents();
    int getOpenIncidents();
    int getClosedIncidents();
    int getResolvedIncidents();
    int getBreachedCount();

    // ================= AUDIT =================
    List<IncidentAuditResponse> getIncidentAudit(Long incidentId);


}