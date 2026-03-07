package com.vamsi.incident_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incident_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= INCIDENT RELATION =================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    // ================= USER RELATION =================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ================= COMMENT MESSAGE =================
    @Column(nullable = false, length = 1000)
    private String message;

    // ================= CREATED TIME =================
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ================= AUTO TIMESTAMP =================
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}