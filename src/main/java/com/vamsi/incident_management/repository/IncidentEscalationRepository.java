package com.vamsi.incident_management.repository;

import com.vamsi.incident_management.entity.IncidentEscalation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncidentEscalationRepository extends JpaRepository<IncidentEscalation, Long> {

    // ✅ Full history (latest first)
    List<IncidentEscalation> findByIncident_IdOrderByCreatedAtDesc(Long incidentId);


    Optional<IncidentEscalation>
    findTopByIncidentIdOrderByLevelDesc(Long incidentId);

    // ⚠️ Legacy check (use carefully)
    Optional<IncidentEscalation> findByIncident_IdAndLevel(Long incidentId, Integer level);

    // ✅ Count escalations (debug / analytics)
    long countByIncident_Id(Long incidentId);
}