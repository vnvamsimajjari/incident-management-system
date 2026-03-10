package com.vamsi.incident_management.listener;

import com.vamsi.incident_management.entity.Incident;
import com.vamsi.incident_management.event.IncidentCreatedEvent;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlaListener {

    private static final Logger log =
            LoggerFactory.getLogger(SlaListener.class);

    @EventListener
    public void handleIncidentCreated(IncidentCreatedEvent event) {

        Incident incident = event.getIncident();

        log.info(
                "SLA monitoring started for Incident {} with priority {}",
                incident.getId(),
                incident.getPriority()
        );
    }


}
