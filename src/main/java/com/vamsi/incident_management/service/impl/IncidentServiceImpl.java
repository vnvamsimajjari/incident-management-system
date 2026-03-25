// 🔥 FULL CLEAN VERSION (STRUCTURED, SAFE, COMPLETE)

package com.vamsi.incident_management.service.impl;

import com.vamsi.incident_management.dto.*;
import com.vamsi.incident_management.entity.*;
import com.vamsi.incident_management.event.*;
import com.vamsi.incident_management.exception.ResourceNotFoundException;
import com.vamsi.incident_management.repository.*;
import com.vamsi.incident_management.service.IncidentService;
import com.vamsi.incident_management.specification.IncidentSpecification;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

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

        String user = getLoggedInUsername();
        boolean admin = isAdmin();

        String assigned = request.getAssignedTo();

        if (!admin && assigned != null && !assigned.equals(user)) {
            throw new AccessDeniedException("Engineers can assign only to themselves");
        }

        String finalUser = admin ? assigned : user;

        User assignedUser = userRepository.findByUsername(finalUser)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", finalUser));

        LocalDateTime now = LocalDateTime.now();

//        if (request.getSlaMinutes() == null || request.getSlaMinutes() <= 0) {
//            throw new IllegalArgumentException("SLA minutes must be greater than 0");
//        }

        //LocalDateTime dueAt = now.plusMinutes(request.getSlaMinutes());
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
                //.dueAt(calculateDueDate(request.getPriority()))
                .dueAt(dueAt)
                .breached(false)
                .build();

        Incident saved = repository.save(incident);

        publisher.publishEvent(new IncidentCreatedEvent(saved));

        return mapToResponse(saved);
    }

    // ================= GET =================
    @Override
    public IncidentResponse getIncident(Long id) {
        Incident incident = getIncidentOrThrow(id);

        if (incident.isDeleted()) {
            throw new ResourceNotFoundException("Incident", "id", id);
        }

        enforceOwnershipForRead(incident);

        return mapToResponse(incident);
    }

    @Override
    public List<IncidentResponse> getBreachedIncidents() {

        return repository.findByBreachedTrueAndDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public List<IncidentResponse> getMyIncidents() {

        String username = getLoggedInUsername();

        return repository.findByAssignedToUsernameAndDeletedFalse(username)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= FILTER =================
    @Override
    public Page<IncidentResponse> getAllIncidents(
            Status status, Priority priority, Boolean breached, Pageable pageable) {

        return repository.findAll(
                IncidentSpecification.filter(status, priority, breached,
                        getLoggedInUsername(), !isAdmin()),
                pageable
        ).map(this::mapToResponse);
    }

    // ================= UPDATE =================
    @Override
    public IncidentResponse updateIncident(Long id, IncidentRequest request) {

        Incident incident = getIncidentOrThrow(id);
        enforceOwnershipForWrite(incident);

        String user = getLoggedInUsername();

        if (request.getAssignedTo() != null) {

            if (!isAdmin() && !request.getAssignedTo().equals(user)) {
                throw new AccessDeniedException("Not allowed to assign others");
            }

            User assigned = userRepository.findByUsername(request.getAssignedTo())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User", "username", request.getAssignedTo()));

            incident.setAssignedTo(assigned);
        }

        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setPriority(request.getPriority());
        incident.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(repository.save(incident));
    }

    // ================= STATUS =================
    @Override
    public IncidentResponse updateIncidentStatus(Long id, Status status) {

        Incident incident = getIncidentOrThrow(id);
        enforceOwnershipForWrite(incident);

        Status old = incident.getStatus();

        validateStatusTransition(old, status);

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

    // ================= RESOLVE =================
    @Override
    public IncidentResponse resolveIncident(Long id) {

        Incident incident = getIncidentOrThrow(id);
        enforceOwnershipForWrite(incident);

        if (!Status.IN_PROGRESS.equals(incident.getStatus())) {
            throw new IllegalStateException("Must be IN_PROGRESS to resolve");
        }

        incident.setStatus(Status.RESOLVED);
        incident.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(repository.save(incident));
    }

    // ================= CLOSE =================
    @Override
    public IncidentResponse closeIncident(Long id) {

        Incident incident = getIncidentOrThrow(id);

        if (!isAdmin()) {
            throw new AccessDeniedException("Only admin can close");
        }

        if (!Status.RESOLVED.equals(incident.getStatus())) {
            throw new IllegalStateException("Only RESOLVED can be closed");
        }

        incident.setStatus(Status.CLOSED);
        incident.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(repository.save(incident));
    }

    // ================= ASSIGN =================
    @Override
    public IncidentResponse assignIncident(Long id, String username) {

        Incident incident = getIncidentOrThrow(id);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        incident.setAssignedTo(user);
        incident.setUpdatedAt(LocalDateTime.now());

        Incident saved = repository.save(incident);

        publisher.publishEvent(new IncidentAssignedEvent(saved));

        return mapToResponse(saved);
    }

    // ================= COMMENTS =================
    @Override
    public IncidentComment addComment(Long id, String message, String username) {

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }

        Incident incident = getIncidentOrThrow(id);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return commentRepository.save(IncidentComment.builder()
                .incident(incident)
                .user(user)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Override
    public List<IncidentComment> getComments(Long id) {
        getIncidentOrThrow(id);
        return commentRepository.findByIncidentId(id);
    }

    // ================= DASHBOARD COUNT =================

    @Override
    public int getTotalIncidents() {
        return (int) repository.count();
    }

    @Override
    public int getOpenIncidents() {
        return repository.countByStatus(Status.OPEN);
    }

    @Override
    public int getClosedIncidents() {
        return repository.countByStatus(Status.CLOSED);
    }

    @Override
    public int getResolvedIncidents() {
        return repository.countByStatus(Status.RESOLVED);
    }

    @Override
    public int getBreachedCount() {
        return repository.findByBreachedTrueAndDeletedFalse().size();
    }
    // ================= AUDIT =================
    @Override
    public List<IncidentAuditResponse> getIncidentAudit(Long incidentId) {

        return auditRepository.findByIncidentIdOrderByChangedAtDesc(incidentId)
                .stream()
                .map(audit -> IncidentAuditResponse.builder()
                        .id(audit.getId())
                        .oldStatus(audit.getOldStatus())
                        .newStatus(audit.getNewStatus())
                        .changedAt(audit.getChangedAt())
                        .build())
                .toList();
    }

    // ================= DELETE =================
    @Override
    public void deleteIncident(Long id) {

        Incident incident = getIncidentOrThrow(id);

        incident.setDeleted(true);
        incident.setUpdatedAt(LocalDateTime.now());

        repository.save(incident);
    }

    // ================= RESTORE =================
    @Override
    public IncidentResponse restoreIncident(Long id) {

        Incident incident = getIncidentOrThrow(id);

        incident.setDeleted(false);
        incident.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(repository.save(incident));
    }

    // ================= UTIL =================
    private String getLoggedInUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Incident getIncidentOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident", "id", id));
    }

    private LocalDateTime calculateDueDate(Priority priority) {
        return switch (priority) {
            case HIGH -> LocalDateTime.now().plusHours(2);
            case MEDIUM -> LocalDateTime.now().plusHours(6);
            case LOW -> LocalDateTime.now().plusHours(12);
            default -> throw new IllegalArgumentException("Invalid priority: " + priority);
        };
    }

    private void validateStatusTransition(Status current, Status next) {
        if (current == next) return;

        switch (current) {
            case OPEN -> { if (next != Status.IN_PROGRESS) throw new IllegalArgumentException("Invalid"); }
            case IN_PROGRESS -> { if (next != Status.RESOLVED) throw new IllegalArgumentException("Invalid"); }
            case RESOLVED -> { if (next != Status.CLOSED) throw new IllegalArgumentException("Invalid"); }
            case CLOSED -> throw new IllegalArgumentException("Closed cannot change");
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
                .assignedTo(i.getAssignedTo() != null ? i.getAssignedTo().getUsername() : null)
                .breached(i.isBreached())
                .build();
    }
    private int getDefaultSlaMinutes(Priority priority) {
        return switch (priority) {
            case HIGH -> 120;    // 2 hours
            case MEDIUM -> 360;  // 6 hours
            case LOW -> 720;     // 12 hours
            default -> 60;
        };
    }
    private void enforceOwnershipForRead(Incident i) {
        if (isAdmin()) return;

        if (i.getAssignedTo() == null ||
                !i.getAssignedTo().getUsername().equals(getLoggedInUsername())) {
            throw new AccessDeniedException("Not allowed");
        }
    }

    private void enforceOwnershipForWrite(Incident i) {
        if (isAdmin()) return;

        if (i.getAssignedTo() == null ||
                !i.getAssignedTo().getUsername().equals(getLoggedInUsername())) {
            throw new AccessDeniedException("Not allowed");
        }
    }
}