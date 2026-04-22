package com.vamsi.incident_management.controller;

import com.vamsi.incident_management.dto.*;
import com.vamsi.incident_management.entity.*;
import com.vamsi.incident_management.service.IncidentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService service;

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

        return ResponseEntity.ok(service.getIncidentById(id));
    }

    // ================= LIST =================
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

    // ================= UPDATE STATUS =================
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ResponseEntity<IncidentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody IncidentStatusUpdateRequest request) {

        return ResponseEntity.ok(
                service.updateIncidentStatus(id, request.getStatus()));
    }
}