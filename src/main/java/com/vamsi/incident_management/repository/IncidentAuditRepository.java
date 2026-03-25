package com.vamsi.incident_management.repository;

import com.vamsi.incident_management.entity.IncidentAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentAuditRepository extends JpaRepository<IncidentAudit, Long> {

    // Latest changes first (recommended for audit view)
    List<IncidentAudit> findByIncidentIdOrderByChangedAtDesc(Long incidentId);

    // Optional: keep ascending if needed elsewhere
    List<IncidentAudit> findByIncidentIdOrderByChangedAtAsc(Long incidentId);
}