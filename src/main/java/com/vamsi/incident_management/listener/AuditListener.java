package com.vamsi.incident_management.listener;

import com.vamsi.incident_management.entity.Incident;
import com.vamsi.incident_management.entity.IncidentAudit;
import com.vamsi.incident_management.event.IncidentAssignedEvent;
import com.vamsi.incident_management.event.IncidentCreatedEvent;
import com.vamsi.incident_management.repository.IncidentAuditRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuditListener {


    private static final Logger log =
            LoggerFactory.getLogger(AuditListener.class);

    private final IncidentAuditRepository auditRepository;

    @EventListener
    public void handleIncidentCreated(IncidentCreatedEvent event) {

        Incident incident = event.getIncident();

        IncidentAudit audit = IncidentAudit.builder()
                .incidentId(incident.getId())
                .oldStatus(null)
                .newStatus(incident.getStatus())
                .changedAt(LocalDateTime.now())
                .build();

        auditRepository.save(audit);

        log.info(
                "Audit created: Incident {} created with status {}",
                incident.getId(),
                incident.getStatus()
        );
    }

    @EventListener
    public void handleIncidentAssigned(IncidentAssignedEvent event) {

        Incident incident = event.getIncident();

        IncidentAudit audit = IncidentAudit.builder()
                .incidentId(incident.getId())
                .oldStatus(null)
                .newStatus(incident.getStatus())
                .changedAt(LocalDateTime.now())
                .build();

        auditRepository.save(audit);

        String assignedUser =
                incident.getAssignedTo() != null
                        ? incident.getAssignedTo().getUsername()
                        : "Unknown";

        log.info(
                "Audit created: Incident {} assigned to {}",
                incident.getId(),
                assignedUser
        );
    }

}
