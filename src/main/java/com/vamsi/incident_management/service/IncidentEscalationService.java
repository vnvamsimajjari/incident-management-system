package com.vamsi.incident_management.service;

import com.vamsi.incident_management.entity.Incident;
import com.vamsi.incident_management.entity.IncidentEscalation;
import com.vamsi.incident_management.exception.ResourceNotFoundException;
import com.vamsi.incident_management.repository.IncidentEscalationRepository;
import com.vamsi.incident_management.repository.IncidentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentEscalationService {

    @Autowired
    private IncidentEscalationRepository escalationRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    // ✅ CREATE ESCALATION
    public IncidentEscalation createEscalation(Long incidentId, Integer level, String reason) {

        // 1. Validate incident
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Incident not found with ID: " + incidentId));

        // 2. Validate level
        if (level == null || level < 1 || level > 3) {
            throw new IllegalArgumentException("Invalid escalation level (only 1,2,3 allowed)");
        }

        // 3. Validate reason
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be empty");
        }

        // 4. Duplicate check
        escalationRepository.findByIncident_IdAndLevel(incidentId, level)
                .ifPresent(e -> {
                    throw new IllegalArgumentException(
                            "Escalation already exists for level " + level);
                });

        // 5. Create escalation
        IncidentEscalation escalation = IncidentEscalation.builder()
                .incident(incident)
                .level(level)
                .reason(reason.trim())
                .build();

        return escalationRepository.save(escalation);
    }

    // ✅ GET ESCALATIONS
    public List<IncidentEscalation> getEscalations(Long incidentId) {

        if (incidentId == null) {
            throw new IllegalArgumentException("Incident ID cannot be null");
        }

        return escalationRepository.findByIncident_IdOrderByCreatedAtDesc(incidentId);
    }
}