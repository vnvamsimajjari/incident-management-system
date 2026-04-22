package com.vamsi.incident_management.service.impl;

import com.vamsi.incident_management.dto.*;
import com.vamsi.incident_management.entity.*;
import com.vamsi.incident_management.event.*;
import com.vamsi.incident_management.exception.ResourceNotFoundException;
import com.vamsi.incident_management.repository.*;
import com.vamsi.incident_management.service.IncidentService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository repository;
    private final IncidentAuditRepository auditRepository;
    private final UserRepository userRepository;
    private final IncidentCommentRepository commentRepository;
    private final ApplicationEventPublisher publisher;

    // ================= CREATE =================
    @Override
    public IncidentResponse createIncident(IncidentRequest request) {

        User assignedUser = userRepository.findByUsername(request.getAssignedTo())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "username", request.getAssignedTo()));

        LocalDateTime now = LocalDateTime.now();

        int slaMinutes = (request.getSlaMinutes() != null && request.getSlaMinutes() > 0)
                ? request.getSlaMinutes()
                : getDefaultSlaMinutes(request.getPriority());

        LocalDateTime dueAt = now.plusMinutes(slaMinutes);

        Incident incident = Incident.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(Status.OPEN)
                .assignedTo(assignedUser)
                .createdAt(now)
                .updatedAt(now)
                .dueAt(dueAt)
                .breached(false)
                .build();

        Incident saved = repository.save(incident);

        publisher.publishEvent(new IncidentCreatedEvent(saved));

        return mapToResponse(saved);
    }

    // ================= GET ALL =================
    @Override
    public Page<IncidentResponse> getAllIncidents(
            Status status, Priority priority, Boolean breached, Pageable pageable) {

        return repository.findAll(pageable)
                .map(this::mapToResponse);
    }

    // ================= STATUS UPDATE =================
    @Override
    public IncidentResponse updateIncidentStatus(Long id, Status status) {

        Incident incident = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident", "id", id));

        Status old = incident.getStatus();

        validateStatusTransition(old, status);

        // audit
        if (old != status) {
            auditRepository.save(IncidentAudit.builder()
                    .incidentId(id)
                    .oldStatus(old)
                    .newStatus(status)
                    .changedAt(LocalDateTime.now())
                    .build());
        }

        incident.setStatus(status);
        incident.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(repository.save(incident));
    }

    // ================= UTIL =================
    private void validateStatusTransition(Status current, Status next) {

        if (current == next) return;

        switch (current) {

            case OPEN -> {
                if (next != Status.IN_PROGRESS)
                    throw new IllegalArgumentException("OPEN → only IN_PROGRESS allowed");
            }

            case IN_PROGRESS -> {
                if (next != Status.RESOLVED)
                    throw new IllegalArgumentException("IN_PROGRESS → only RESOLVED allowed");
            }

            case RESOLVED -> {
                if (next != Status.CLOSED)
                    throw new IllegalArgumentException("RESOLVED → only CLOSED allowed");
            }

            case CLOSED -> throw new IllegalArgumentException("CLOSED cannot be changed");
        }
    }

    private IncidentResponse mapToResponse(Incident i) {
        return IncidentResponse.builder()
                .id(i.getId())
                .title(i.getTitle())
                .description(i.getDescription())
                .priority(i.getPriority())
                .status(i.getStatus())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .assignedTo(i.getAssignedTo() != null
                        ? i.getAssignedTo().getUsername()
                        : null)
                .breached(i.isBreached())
                .build();
    }

    private int getDefaultSlaMinutes(Priority priority) {
        return switch (priority) {
            case HIGH -> 120;
            case MEDIUM -> 360;
            case LOW -> 720;
            default -> 60;
        };
    }
}