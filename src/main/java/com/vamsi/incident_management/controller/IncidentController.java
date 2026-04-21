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
    public ResponseEntity<IncidentResponse> create(@Valid @RequestBody IncidentRequest request) {
        return ResponseEntity.ok(service.createIncident(request));
    }

    // ================= GET =================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<IncidentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getIncident(id));
    }

    // ================= LIST =================
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<Page<IncidentResponse>> getAll(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Boolean breached,
            Pageable pageable) {

        return ResponseEntity.ok(service.getAllIncidents(status, priority, breached, pageable));
    }

    // ================= UPDATE FULL =================
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<IncidentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody IncidentRequest request) {

        return ResponseEntity.ok(service.updateIncident(id, request));
    }

    // ================= UPDATE STATUS (🔥 MAIN FIX) =================
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<IncidentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody IncidentStatusUpdateRequest request) {

        return ResponseEntity.ok(
                service.updateIncidentStatus(id, request.getStatus()));
    }

    // ================= ASSIGN =================
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<IncidentResponse> assignIncident(
            @PathVariable Long id,
            @Valid @RequestBody AssignRequest request) {

        return ResponseEntity.ok(
                service.assignIncident(id, request.getAssignedEngineerUsername()));
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

    // ❌ DELETE REMOVED

    // ================= COMMENTS =================
    @PostMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<IncidentComment> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            Authentication auth) {

        return ResponseEntity.ok(
                service.addComment(id, request.getMessage(), auth.getName()));
    }

    @GetMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<List<IncidentComment>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(service.getComments(id));
    }

    // ================= EXTRA =================
    @GetMapping("/breached")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<List<IncidentResponse>> getBreachedIncidents() {
        return ResponseEntity.ok(service.getBreachedIncidents());
    }

    @GetMapping("/my-incidents")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<List<IncidentResponse>> getMyIncidents() {
        return ResponseEntity.ok(service.getMyIncidents());
    }
}