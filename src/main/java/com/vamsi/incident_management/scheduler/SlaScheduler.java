package com.vamsi.incident_management.scheduler;

import com.vamsi.incident_management.entity.Incident;
import com.vamsi.incident_management.entity.Status;
import com.vamsi.incident_management.repository.IncidentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SlaScheduler {

    private final IncidentRepository repository;

    @Scheduled(fixedRate = 60000) // every 1 minute
    public void checkSlaBreach() {

        List<Incident> incidents = repository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (Incident incident : incidents) {

            // skip invalid
            if (incident.getDueAt() == null) continue;

            // skip already breached
            if (incident.isBreached()) continue;

            // skip closed/resolved
            if (incident.getStatus() == Status.CLOSED ||
                    incident.getStatus() == Status.RESOLVED) continue;

            // check breach
            if (incident.getDueAt().isBefore(now)) {

                incident.setBreached(true);
                repository.save(incident);

                System.out.println("✅ SLA Breached: " + incident.getId());
            }
        }
    }
}