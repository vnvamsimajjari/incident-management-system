package com.vamsi.incident_management.scheduler;

import com.vamsi.incident_management.entity.Incident;
import com.vamsi.incident_management.repository.IncidentRepository;

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

    /**
     * Runs every 1 minute
     * - Marks incidents as breached
     */
    @Scheduled(fixedRate = 60000)
    public void checkSlaBreaches() {

        log.info("SLA Breach Scheduler started...");

        try {
            LocalDateTime now = LocalDateTime.now();

            List<Incident> incidents =
                    incidentRepository.findByBreachedFalseAndDueAtBefore(now);

            for (Incident incident : incidents) {
                incident.setBreached(true);
            }

            incidentRepository.saveAll(incidents);

            log.info("Breached incidents count: {}", incidents.size());

        } catch (Exception ex) {
            log.error("Error in SLA breach scheduler", ex);
        }

        log.info("SLA Breach Scheduler completed.");
    }
}