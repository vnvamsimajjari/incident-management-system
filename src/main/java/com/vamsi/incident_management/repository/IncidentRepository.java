package com.vamsi.incident_management.repository;

import com.vamsi.incident_management.entity.Incident;
import com.vamsi.incident_management.entity.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IncidentRepository
        extends JpaRepository<Incident, Long>,
        JpaSpecificationExecutor<Incident> {

    // ================= PAGINATION + FETCH OPTIMIZATION =================
    @EntityGraph(attributePaths = {"assignedTo"})
    Page<Incident> findAll(Specification<Incident> spec, Pageable pageable);

    // ================= SLA BREACH DETECTION =================
    @Query("""
        SELECT i FROM Incident i
        WHERE i.breached = false
        AND i.deleted = false
        AND i.status <> 'RESOLVED'
        AND i.dueAt < :now
    """)
    List<Incident> findIncidentsWithBreachedSla(@Param("now") LocalDateTime now);

    // ================= INCIDENT LIST HELPERS =================
    List<Incident> findByBreachedTrueAndDeletedFalse();

    List<Incident> findByAssignedToUsernameAndDeletedFalse(String username);

    // ================= COUNT METHODS (FOR DASHBOARD SUPPORT) =================
    int countByStatus(Status status);
    @Query("SELECT i.status, COUNT(i) FROM Incident i WHERE i.deleted = false GROUP BY i.status")
    List<Object[]> countByStatusGroup();

    @Query("SELECT i.priority, COUNT(i) FROM Incident i WHERE i.deleted = false GROUP BY i.priority")
    List<Object[]> countByPriorityGroup();
}