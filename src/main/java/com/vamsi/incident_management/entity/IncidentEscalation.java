package com.vamsi.incident_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incident_escalations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentEscalation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long incidentId;

    @Enumerated(EnumType.STRING)
    private Priority oldPriority;

    @Enumerated(EnumType.STRING)
    private Priority newPriority;

    private LocalDateTime escalatedAt;
}