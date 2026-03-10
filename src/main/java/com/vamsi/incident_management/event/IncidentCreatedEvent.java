package com.vamsi.incident_management.event;

import com.vamsi.incident_management.entity.Incident;

public class IncidentCreatedEvent {

    private final Incident incident;

    public IncidentCreatedEvent(Incident incident) {
        this.incident = incident;
    }

    public Incident getIncident() {
        return incident;
    }
}