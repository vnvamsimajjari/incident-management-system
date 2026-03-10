package com.vamsi.incident_management.repository;

import com.vamsi.incident_management.entity.IncidentEscalation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentEscalationRepository
        extends JpaRepository<IncidentEscalation, Long> {

    List<IncidentEscalation> findByIncidentIdOrderByEscalatedAtDesc(Long incidentId);
}