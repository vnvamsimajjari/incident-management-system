package com.vamsi.incident_management.controller;

import com.vamsi.incident_management.dto.*;
import com.vamsi.incident_management.entity.*;
import com.vamsi.incident_management.repository.IncidentEscalationRepository;
import com.vamsi.incident_management.repository.NotificationLogRepository;
import com.vamsi.incident_management.service.IncidentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService service;
    private final IncidentEscalationRepository escalationRepository;
    private final NotificationLogRepository notificationLogRepository;

    // ================= CREATE =================
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<IncidentResponse> create(
            @Valid @RequestBody IncidentRequest request) {

        return ResponseEntity.ok(service.createIncident(request));
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<IncidentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getIncident(id));
    }

    // ================= GET AUDIT =================
    @GetMapping("/{id}/audit")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<List<IncidentAuditResponse>> getAudit(@PathVariable Long id) {
        return ResponseEntity.ok(service.getIncidentAudit(id));
    }

    // ================= FILTER + PAGINATION =================
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<Page<IncidentResponse>> getAll(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Boolean breached,
            Pageable pageable) {

        return ResponseEntity.ok(
                service.getAllIncidents(status, priority, breached, pageable));
    }

    // ================= UPDATE INCIDENT =================
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<IncidentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody IncidentRequest request) {

        return ResponseEntity.ok(service.updateIncident(id, request));
    }

    // ================= ASSIGN INCIDENT =================
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<IncidentResponse> assignIncident(
            @PathVariable Long id,
            @Valid @RequestBody AssignRequest request) {

        return ResponseEntity.ok(
                service.assignIncident(id, request.getAssignedEngineerUsername()));
    }

    // ================= STATUS UPDATE =================
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<IncidentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody IncidentStatusUpdateRequest request) {

        return ResponseEntity.ok(
                service.updateIncidentStatus(id, request.getStatus()));
    }

    // ================= RESOLVE =================
    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<IncidentResponse> resolve(@PathVariable Long id) {
        return ResponseEntity.ok(service.resolveIncident(id));
    }

    // ================= CLOSE =================
    @PutMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IncidentResponse> close(@PathVariable Long id) {
        return ResponseEntity.ok(service.closeIncident(id));
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

        return ResponseEntity.ok(service.restoreIncident(id));
    }

    // ================= BREACHED INCIDENTS =================
    @GetMapping("/breached")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<List<IncidentResponse>> getBreachedIncidents() {
        return ResponseEntity.ok(service.getBreachedIncidents());
    }

    // ================= MY INCIDENTS =================
    @GetMapping("/my-incidents")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<List<IncidentResponse>> getMyIncidents() {
        return ResponseEntity.ok(service.getMyIncidents());
    }

    // ================= ADD COMMENT =================
    @PostMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<IncidentComment> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            Authentication auth) {

        return ResponseEntity.ok(
                service.addComment(id, request.getMessage(), auth.getName()));
    }

    // ================= GET COMMENTS =================
    @GetMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<List<IncidentComment>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(service.getComments(id));
    }

    // ================= ESCALATION HISTORY =================
    @GetMapping("/{id}/escalations")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<List<IncidentEscalation>> getEscalations(@PathVariable Long id) {

        return ResponseEntity.ok(
                escalationRepository.findByIncident_IdOrderByCreatedAtDesc(id));
    }

    // ================= NOTIFICATION LOGS =================
    @GetMapping("/notifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationLog>> getNotificationLogs() {

        return ResponseEntity.ok(notificationLogRepository.findAll());
    }
}