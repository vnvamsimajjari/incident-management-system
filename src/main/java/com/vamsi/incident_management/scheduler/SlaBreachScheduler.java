package com.vamsi.incident_management.scheduler;

import com.vamsi.incident_management.entity.*;
import com.vamsi.incident_management.repository.*;
import com.vamsi.incident_management.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlaBreachScheduler {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final IncidentEscalationRepository escalationRepository;

    // Runs every 5 minutes
    @Scheduled(cron = "0 */5 * * * *")
    public void checkSlaBreaches() {

        log.info("Running SLA breach check...");

        LocalDateTime now = LocalDateTime.now();

        List<Incident> incidents =
                incidentRepository.findIncidentsWithBreachedSla(now);

        if (incidents.isEmpty()) {
            log.info("No SLA breaches detected.");
            return;
        }

        for (Incident incident : incidents) {

            incident.setBreached(true);

            escalateIncident(incident);

            incidentRepository.save(incident);

            sendNotification(incident);

            log.warn("SLA breached and processed for incident {}", incident.getId());
        }

        log.info("SLA breach check completed. {} incidents updated.", incidents.size());
    }

    private void escalateIncident(Incident incident) {

        Priority oldPriority = incident.getPriority();
        Priority newPriority = oldPriority;

        switch (oldPriority) {

            case LOW:
                newPriority = Priority.MEDIUM;
                incident.setPriority(newPriority);
                log.info("Incident {} escalated from LOW → MEDIUM", incident.getId());
                break;

            case MEDIUM:
                newPriority = Priority.HIGH;
                incident.setPriority(newPriority);
                log.info("Incident {} escalated from MEDIUM → HIGH", incident.getId());
                break;

            case HIGH:
                User admin = userRepository.findByUsername("admin")
                        .orElseThrow(() -> new RuntimeException("Admin user not found"));

                incident.setAssignedTo(admin);
                log.info("Incident {} escalated to ADMIN", incident.getId());
                break;
        }

        // Save escalation history
        if (oldPriority != newPriority) {

            IncidentEscalation escalation = IncidentEscalation.builder()
                    .incidentId(incident.getId())
                    .oldPriority(oldPriority)
                    .newPriority(newPriority)
                    .escalatedAt(LocalDateTime.now())
                    .build();

            escalationRepository.save(escalation);
        }
    }

    private void sendNotification(Incident incident) {

        try {

            if (incident.getAssignedTo() != null &&
                    incident.getAssignedTo().getEmail() != null) {

                String email = incident.getAssignedTo().getEmail();

                emailService.sendSlaBreachEmail(
                        email,
                        incident.getId()
                );
            }

        } catch (Exception e) {
            log.error("Failed to send SLA notification for incident {}", incident.getId(), e);
        }
    }
}