package com.vamsi.incident_management.repository;

import com.vamsi.incident_management.entity.Incident;
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

    // Pagination + fetch assigned user to avoid N+1 problem
    @EntityGraph(attributePaths = {"assignedTo"})
    Page<Incident> findAll(Specification<Incident> spec, Pageable pageable);

    // Optimized query for SLA breach detection
    @Query("""
        SELECT i FROM Incident i
        WHERE i.breached = false
        AND i.deleted = false
        AND i.status <> 'RESOLVED'
        AND i.dueAt < :now
    """)
    List<Incident> findIncidentsWithBreachedSla(@Param("now") LocalDateTime now);

    // Dashboard: breached incidents
    @Query("""
        SELECT i FROM Incident i
        WHERE i.breached = true
        AND i.deleted = false
    """)
    List<Incident> findBreachedIncidents();

    // Dashboard: incidents assigned to logged-in user
    @Query("""
        SELECT i FROM Incident i
        WHERE i.assignedTo.username = :username
        AND i.deleted = false
    """)
    List<Incident> findIncidentsAssignedTo(@Param("username") String username);

    // Dashboard summary counts
    @Query("""
        SELECT COUNT(i) FROM Incident i
        WHERE i.deleted = false
    """)
    Long countAllActive();

    @Query("""
        SELECT COUNT(i) FROM Incident i
        WHERE i.status = 'OPEN'
        AND i.deleted = false
    """)
    Long countOpen();

    @Query("""
        SELECT COUNT(i) FROM Incident i
        WHERE i.status = 'RESOLVED'
        AND i.deleted = false
    """)
    Long countResolved();

    @Query("""
        SELECT COUNT(i) FROM Incident i
        WHERE i.breached = true
        AND i.deleted = false
    """)
    Long countBreached();
}