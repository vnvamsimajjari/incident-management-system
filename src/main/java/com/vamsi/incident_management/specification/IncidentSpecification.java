package com.vamsi.incident_management.specification;

import com.vamsi.incident_management.entity.Incident;
import com.vamsi.incident_management.entity.Priority;
import com.vamsi.incident_management.entity.Status;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class IncidentSpecification {

    public static Specification<Incident> filter(
            Status status,
            Priority priority,
            Boolean breached,
            String assignedTo,
            boolean restrictByOwner) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Hide soft-deleted incidents
            predicates.add(cb.isFalse(root.get("deleted")));

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }

            if (breached != null) {
                predicates.add(cb.equal(root.get("breached"), breached));
            }

            // Ownership filter (compare by username of assigned user)
            if (restrictByOwner && assignedTo != null) {
                predicates.add(cb.equal(root.get("assignedTo").get("username"), assignedTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}