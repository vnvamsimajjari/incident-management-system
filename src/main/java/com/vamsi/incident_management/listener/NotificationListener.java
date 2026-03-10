package com.vamsi.incident_management.listener;

import com.vamsi.incident_management.entity.Incident;
import com.vamsi.incident_management.event.IncidentAssignedEvent;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {


    private static final Logger log =
            LoggerFactory.getLogger(NotificationListener.class);

    @EventListener
    public void handleIncidentAssigned(IncidentAssignedEvent event) {

        Incident incident = event.getIncident();

        String assignedUser =
                incident.getAssignedTo() != null
                        ? incident.getAssignedTo().getUsername()
                        : "Unknown";

        log.info(
                "Notification triggered: Incident {} assigned to {}",
                incident.getId(),
                assignedUser
        );
    }


}
