package com.vamsi.incident_management.event;

import com.vamsi.incident_management.entity.Incident;

public class IncidentAssignedEvent {

    private final Incident incident;

    public IncidentAssignedEvent(Incident incident) {
        this.incident = incident;
    }

    public Incident getIncident() {
        return incident;
    }
}