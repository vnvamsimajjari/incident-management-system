package com.vamsi.incident_management.service.impl;

import com.vamsi.incident_management.dto.*;
import com.vamsi.incident_management.entity.*;
import com.vamsi.incident_management.event.IncidentAssignedEvent;
import com.vamsi.incident_management.event.IncidentCreatedEvent;
import com.vamsi.incident_management.exception.ResourceNotFoundException;
import com.vamsi.incident_management.repository.*;
import com.vamsi.incident_management.service.IncidentService;
import com.vamsi.incident_management.specification.IncidentSpecification;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final IncidentCommentRepository incidentCommentRepository;
    private final ApplicationEventPublisher publisher;

    // ================= CREATE INCIDENT =================
    @Override
    public IncidentResponse createIncident(IncidentRequest request) {

        String loggedInUser = getLoggedInUsername();
        boolean admin = isAdmin();

        String assignedUsername = request.getAssignedTo();

        if (!admin && assignedUsername != null && !assignedUsername.equals(loggedInUser)) {
            throw new AccessDeniedException(
                    "Engineers can create incidents only assigned to themselves");
        }

        String finalAssignedUser = admin ? assignedUsername : loggedInUser;

        User user = userRepository.findByUsername(finalAssignedUser)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDateTime now = LocalDateTime.now();

        Incident incident = Incident.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(Status.OPEN)
                .assignedTo(user)
                .createdAt(now)
                .updatedAt(now)
                .dueAt(calculateDueDate(request.getPriority()))
                .breached(false)
                .build();

        Incident savedIncident = repository.save(incident);

        publisher.publishEvent(new IncidentCreatedEvent(savedIncident));

        return mapToResponse(savedIncident);
    }

    // ================= GET INCIDENT =================
    @Override
    public IncidentResponse getIncident(Long id) {

        Incident incident = getIncidentOrThrow(id);

        if (incident.isDeleted()) {
            throw new ResourceNotFoundException("Incident not found with id: " + id);
        }

        enforceOwnershipForRead(incident);

        return mapToResponse(incident);
    }

    // ================= FILTER INCIDENTS =================
    @Override
    public Page<IncidentResponse> getAllIncidents(
            Status status,
            Priority priority,
            Boolean breached,
            Pageable pageable) {

        String loggedInUser = getLoggedInUsername();
        boolean isAdmin = isAdmin();

        return repository
                .findAll(
                        IncidentSpecification.filter(
                                status,
                                priority,
                                breached,
                                loggedInUser,
                                !isAdmin
                        ),
                        pageable
                )
                .map(this::mapToResponse);
    }

    // ================= UPDATE INCIDENT =================
    @Override
    public IncidentResponse updateIncident(Long id, IncidentRequest request) {

        Incident incident = getIncidentOrThrow(id);
        enforceOwnershipForWrite(incident);

        String loggedInUser = getLoggedInUsername();
        boolean admin = isAdmin();

        if (request.getAssignedTo() != null) {

            if (!admin && !request.getAssignedTo().equals(loggedInUser)) {
                throw new AccessDeniedException(
                        "Engineers can assign incidents only to themselves");
            }

            User user = userRepository.findByUsername(request.getAssignedTo())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            incident.setAssignedTo(user);
        }

        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setPriority(request.getPriority());
        incident.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(repository.save(incident));
    }

    // ================= STATUS UPDATE =================
    @Override
    public IncidentResponse updateIncidentStatus(Long id, Status status) {

        Incident incident = getIncidentOrThrow(id);
        enforceOwnershipForWrite(incident);

        Status oldStatus = incident.getStatus();

        validateStatusTransition(oldStatus, status);

        if (oldStatus != status) {

            IncidentAudit audit = IncidentAudit.builder()
                    .incidentId(id)
                    .oldStatus(oldStatus)
                    .newStatus(status)
                    .changedAt(LocalDateTime.now())
                    .build();

            auditRepository.save(audit);
        }

        incident.setStatus(status);
        incident.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(repository.save(incident));
    }

    // ================= RESOLVE INCIDENT =================
    @Override
    public IncidentResponse resolveIncident(Long id) {

        Incident incident = getIncidentOrThrow(id);
        enforceOwnershipForWrite(incident);

        if (incident.getStatus() != Status.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Only IN_PROGRESS incidents can be resolved");
        }

        incident.setStatus(Status.RESOLVED);
        incident.setResolvedAt(LocalDateTime.now());
        incident.setUpdatedAt(LocalDateTime.now());

        if (incident.getDueAt() != null &&
                LocalDateTime.now().isAfter(incident.getDueAt())) {
            incident.setBreached(true);
        }

        Incident savedIncident = repository.save(incident);

        IncidentAudit audit = IncidentAudit.builder()
                .incidentId(savedIncident.getId())
                .oldStatus(Status.IN_PROGRESS)
                .newStatus(Status.RESOLVED)
                .changedAt(LocalDateTime.now())
                .build();

        auditRepository.save(audit);

        return mapToResponse(savedIncident);
    }

    // ================= CLOSE INCIDENT =================
    @Override
    public IncidentResponse closeIncident(Long id) {

        Incident incident = getIncidentOrThrow(id);

        if (!isAdmin()) {
            throw new AccessDeniedException(
                    "Only admin can close incidents");
        }

        if (incident.getStatus() != Status.RESOLVED) {
            throw new IllegalStateException(
                    "Only RESOLVED incidents can be closed");
        }

        incident.setStatus(Status.CLOSED);
        incident.setUpdatedAt(LocalDateTime.now());

        Incident savedIncident = repository.save(incident);

        return mapToResponse(savedIncident);
    }

    // ================= INCIDENT AUDIT =================
    @Override
    public List<IncidentAuditResponse> getIncidentAudit(Long incidentId) {

        getIncidentOrThrow(incidentId);

        return auditRepository
                .findByIncidentIdOrderByChangedAtAsc(incidentId)
                .stream()
                .map(audit -> IncidentAuditResponse.builder()
                        .oldStatus(audit.getOldStatus())
                        .newStatus(audit.getNewStatus())
                        .changedAt(audit.getChangedAt())
                        .build())
                .toList();
    }

    // ================= DELETE INCIDENT =================
    @Override
    public void deleteIncident(Long id) {

        Incident incident = getIncidentOrThrow(id);
        enforceOwnershipForWrite(incident);

        incident.setDeleted(true);
        incident.setDeletedAt(LocalDateTime.now());
        incident.setDeletedBy(getLoggedInUsername());

        repository.save(incident);
    }

    // ================= RESTORE INCIDENT =================
    @Override
    public IncidentResponse restoreIncident(Long id) {

        Incident incident = getIncidentOrThrow(id);

        if (!incident.isDeleted()) {
            throw new IllegalStateException("Incident is not deleted");
        }

        incident.setDeleted(false);
        incident.setDeletedAt(null);
        incident.setDeletedBy(null);
        incident.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(repository.save(incident));
    }

    // ================= ASSIGN INCIDENT =================
    @Override
    public IncidentResponse assignIncident(Long id, String assignedTo) {

        Incident incident = getIncidentOrThrow(id);

        User user = userRepository.findByUsername(assignedTo)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        incident.setAssignedTo(user);
        incident.setUpdatedAt(LocalDateTime.now());

        Incident savedIncident = repository.save(incident);

        publisher.publishEvent(new IncidentAssignedEvent(savedIncident));

        return mapToResponse(savedIncident);
    }

    // ================= COMMENTS =================
    @Override
    public IncidentComment addComment(Long incidentId, String message, String username) {

        Incident incident = getIncidentOrThrow(incidentId);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        IncidentComment comment = IncidentComment.builder()
                .incident(incident)
                .user(user)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        return incidentCommentRepository.save(comment);
    }

    @Override
    public List<IncidentComment> getComments(Long incidentId) {

        getIncidentOrThrow(incidentId);

        return incidentCommentRepository
                .findByIncidentId(incidentId);
    }

    // ================= DASHBOARD =================
    @Override
    public List<IncidentResponse> getBreachedIncidents() {

        return repository.findBreachedIncidents()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<IncidentResponse> getMyIncidents() {

        String username = getLoggedInUsername();

        return repository.findIncidentsAssignedTo(username)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public DashboardSummaryResponse getDashboardSummary() {

        return DashboardSummaryResponse.builder()
                .totalIncidents(repository.countAllActive())
                .openIncidents(repository.countOpen())
                .resolvedIncidents(repository.countResolved())
                .breachedIncidents(repository.countBreached())
                .build();
    }

    // ================= UTIL =================
    private String getLoggedInUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean isAdmin() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Incident getIncidentOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Incident not found with id: " + id));
    }

    private LocalDateTime calculateDueDate(Priority priority) {

        LocalDateTime now = LocalDateTime.now();

        switch (priority) {
            case HIGH: return now.plusHours(4);
            case MEDIUM: return now.plusHours(8);
            case LOW: return now.plusHours(24);
            default: throw new IllegalArgumentException("Invalid priority: " + priority);
        }
    }

    private void validateStatusTransition(Status current, Status next) {

        if (current == next) return;

        switch (current) {

            case OPEN:
                if (next != Status.IN_PROGRESS)
                    throw new IllegalArgumentException("Invalid transition");
                break;

            case IN_PROGRESS:
                if (next != Status.RESOLVED)
                    throw new IllegalArgumentException("Invalid transition");
                break;

            case RESOLVED:
                if (next != Status.CLOSED)
                    throw new IllegalArgumentException("Invalid transition");
                break;

            case CLOSED:
                throw new IllegalArgumentException("Closed incident cannot change");
        }
    }

    private IncidentResponse mapToResponse(Incident incident) {

        return IncidentResponse.builder()
                .id(incident.getId())
                .title(incident.getTitle())
                .description(incident.getDescription())
                .priority(incident.getPriority())
                .status(incident.getStatus())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .assignedTo(
                        incident.getAssignedTo() != null
                                ? incident.getAssignedTo().getUsername()
                                : null
                )
                .build();
    }

    private void enforceOwnershipForRead(Incident incident) {

        if (isAdmin()) return;

        String loggedInUser = getLoggedInUsername();

        if (incident.getAssignedTo() == null ||
                !incident.getAssignedTo().getUsername().equals(loggedInUser)) {

            throw new AccessDeniedException(
                    "You are not allowed to view this incident");
        }
    }

    private void enforceOwnershipForWrite(Incident incident) {

        if (isAdmin()) return;

        String loggedInUser = getLoggedInUsername();

        if (incident.getAssignedTo() == null ||
                !incident.getAssignedTo().getUsername().equals(loggedInUser)) {

            throw new AccessDeniedException(
                    "You are not allowed to modify this incident");
        }
    }


}
