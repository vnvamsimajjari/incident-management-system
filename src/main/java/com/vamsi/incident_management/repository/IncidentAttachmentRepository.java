package com.vamsi.incident_management.repository;

import com.vamsi.incident_management.entity.IncidentAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentAttachmentRepository extends JpaRepository<IncidentAttachment, Long> {

    // Fetch all attachments belonging to a specific incident
    List<IncidentAttachment> findByIncident_Id(Long incidentId);

}