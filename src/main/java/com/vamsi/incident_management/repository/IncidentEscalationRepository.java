package com.vamsi.incident_management.repository;

import com.vamsi.incident_management.entity.IncidentEscalation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncidentEscalationRepository extends JpaRepository<IncidentEscalation, Long> {

    // ✅ Get all escalations (latest first)
    List<IncidentEscalation> findByIncident_IdOrderByCreatedAtDesc(Long incidentId);

    // ✅ Check duplicate level
    Optional<IncidentEscalation> findByIncident_IdAndLevel(Long incidentId, Integer level);
}