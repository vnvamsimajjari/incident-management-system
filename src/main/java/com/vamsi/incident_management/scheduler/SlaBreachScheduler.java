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

    // Runs every 5 minutes
    @Scheduled(cron = "0 */5 * * * *")
    public void checkSlaBreaches() {

        log.info("Running SLA breach check...");

        LocalDateTime now = LocalDateTime.now();

        // Fetch only incidents that should already be breached
        List<Incident> incidents =
                incidentRepository.findIncidentsWithBreachedSla(now);

        if (incidents.isEmpty()) {
            log.info("No SLA breaches detected.");
            return;
        }

        for (Incident incident : incidents) {

            incident.setBreached(true);

            incidentRepository.save(incident);

            log.warn("SLA breached for incident {}", incident.getId());
        }

        log.info("SLA breach check completed. {} incidents updated.", incidents.size());
    }
}