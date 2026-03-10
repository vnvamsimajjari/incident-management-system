package com.vamsi.incident_management.repository;

import com.vamsi.incident_management.entity.IncidentComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentCommentRepository extends JpaRepository<IncidentComment, Long> {

    // ✅ Fetch comments by incident ID
    List<IncidentComment> findByIncidentId(Long incidentId);
}