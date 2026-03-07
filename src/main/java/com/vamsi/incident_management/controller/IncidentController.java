package com.vamsi.incident_management.controller;

import com.vamsi.incident_management.dto.*;
import com.vamsi.incident_management.entity.IncidentComment;
import com.vamsi.incident_management.entity.Priority;
import com.vamsi.incident_management.entity.Status;
import com.vamsi.incident_management.service.IncidentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService service;

    // ================= CREATE =================
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public IncidentResponse create(@Valid @RequestBody IncidentRequest request) {
        return service.createIncident(request);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public IncidentResponse getById(@PathVariable Long id) {
        return service.getIncident(id);
    }

    // ================= GET AUDIT =================
    @GetMapping("/{id}/audit")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public List<IncidentAuditResponse> getAudit(@PathVariable Long id) {
        return service.getIncidentAudit(id);
    }

    // ================= FILTER + PAGINATION =================
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public Page<IncidentResponse> getAll(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Boolean breached,
            Pageable pageable) {

        return service.getAllIncidents(status, priority, breached, pageable);
    }

    // ================= UPDATE INCIDENT =================
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public IncidentResponse update(
            @PathVariable Long id,
            @Valid @RequestBody IncidentRequest request) {

        return service.updateIncident(id, request);
    }

    // ================= ASSIGN INCIDENT =================
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<IncidentResponse> assignIncident(
            @PathVariable Long id,
            @RequestBody @Valid AssignRequest request) {

        return ResponseEntity.ok(
                service.assignIncident(id, request.getAssignedTo()));
    }

    // ================= STATUS UPDATE =================
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public IncidentResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody IncidentStatusUpdateRequest request) {

        return service.updateIncidentStatus(id, request.getStatus());
    }

    // ================= DELETE INCIDENT =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }

    // ================= RESTORE INCIDENT =================
    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IncidentResponse> restoreIncident(@PathVariable Long id) {

        IncidentResponse response = service.restoreIncident(id);
        return ResponseEntity.ok(response);
    }

    // ================= BREACHED INCIDENTS =================
    @GetMapping("/breached")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public List<IncidentResponse> getBreachedIncidents() {

        return service.getBreachedIncidents();
    }

    // ================= MY INCIDENTS =================
    @GetMapping("/my-incidents")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public List<IncidentResponse> getMyIncidents() {

        return service.getMyIncidents();
    }

    // ================= DASHBOARD SUMMARY =================
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public DashboardSummaryResponse getDashboardSummary() {

        return service.getDashboardSummary();
    }

    // ================= ADD COMMENT =================
    @PostMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public IncidentComment addComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication auth) {

        return service.addComment(id, body.get("message"), auth.getName());
    }

    // ================= GET COMMENTS =================
    @GetMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public List<IncidentComment> getComments(@PathVariable Long id) {

        return service.getComments(id);
    }
}