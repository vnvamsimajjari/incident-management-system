package com.vamsi.incident_management.controller;

import com.vamsi.incident_management.entity.IncidentEscalation;
import com.vamsi.incident_management.service.IncidentEscalationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/escalations")
public class IncidentEscalationController {

    @Autowired
    private IncidentEscalationService escalationService;

    // ✅ CREATE ESCALATION
    @PostMapping
    public ResponseEntity<?> createEscalation(
            @RequestParam Long incidentId,
            @RequestParam Integer level,
            @RequestParam String reason) {

        IncidentEscalation escalation =
                escalationService.createEscalation(incidentId, level, reason);

        return ResponseEntity.ok(
                Map.of(
                        "status", 200,
                        "message", "Escalation created successfully",
                        "id", escalation.getId(),
                        "level", escalation.getLevel(),
                        "createdAt", escalation.getCreatedAt()
                )
        );
    }

    // ✅ GET ESCALATIONS
    @GetMapping("/{incidentId}")
    public ResponseEntity<List<IncidentEscalation>> getEscalations(
            @PathVariable Long incidentId) {

        List<IncidentEscalation> escalations =
                escalationService.getEscalations(incidentId);

        return ResponseEntity.ok(escalations);
    }
}