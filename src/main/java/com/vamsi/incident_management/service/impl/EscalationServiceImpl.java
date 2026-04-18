package com.vamsi.incident_management.service.impl;

import com.vamsi.incident_management.entity.Incident;
import com.vamsi.incident_management.entity.IncidentEscalation;
import com.vamsi.incident_management.repository.IncidentRepository;
import com.vamsi.incident_management.repository.IncidentEscalationRepository;
import com.vamsi.incident_management.service.EscalationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EscalationServiceImpl implements EscalationService {

    private final IncidentRepository incidentRepository;
    private final IncidentEscalationRepository escalationRepository;

    private static final int MAX_LEVEL = 2;

    @Override
    @Transactional
    public void processEscalations() {

        LocalDateTime now = LocalDateTime.now();

        List<Incident> incidents = incidentRepository.findIncidentsForEscalation(now);

        log.info("Escalation check started. Candidates: {}", incidents.size());

        for (Incident incident : incidents) {

            try {

                Optional<IncidentEscalation> lastEscalation =
                        escalationRepository.findTopByIncidentIdOrderByLevelDesc(incident.getId());

                int currentLevel = incident.getEscalationLevel() == null ? 0 : incident.getEscalationLevel();
                int nextLevel = lastEscalation.map(e -> e.getLevel() + 1).orElse(1);

                if (nextLevel > MAX_LEVEL) {
                    continue;
                }

                LocalDateTime lastTime = lastEscalation
                        .map(IncidentEscalation::getEscalatedAt)
                        .orElse(incident.getCreatedAt());

                int delayMinutes = getDelayForLevel(nextLevel);

                if (lastTime.plusMinutes(delayMinutes).isAfter(now)) {
                    continue;
                }

                // SAVE ESCALATION
                IncidentEscalation escalation = new IncidentEscalation();
                escalation.setIncident(incident);
                escalation.setLevel(nextLevel);
                escalation.setOldLevel(currentLevel);
                escalation.setReason("SLA breached");
                escalation.setCreatedAt(now);
                escalation.setEscalatedAt(now);

                escalationRepository.save(escalation);

                // UPDATE INCIDENT
                incident.setEscalationLevel(nextLevel);
                incident.setLastEscalationTime(now);
                incident.setBreached(true);

                incidentRepository.save(incident);

                log.info("Escalated Incident ID {} to Level {}", incident.getId(), nextLevel);

            } catch (Exception ex) {
                log.error("Error processing escalation for incident {}", incident.getId(), ex);
            }
        }

        log.info("Escalation check completed.");
    }

    private int getDelayForLevel(int level) {
        return switch (level) {
            case 1 -> 5;
            case 2 -> 10;
            default -> 0;
        };
    }
}