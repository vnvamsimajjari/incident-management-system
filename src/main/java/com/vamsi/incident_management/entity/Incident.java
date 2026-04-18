package com.vamsi.incident_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // SLA Fields
    private LocalDateTime dueAt;

    private LocalDateTime resolvedAt;

    @Column(nullable = false)
    private boolean breached;

    // Ownership Field
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to", nullable = false)
    @JsonIgnore   // 🔥 FIX ADDED
    private User assignedTo;

    // ---------------- SOFT DELETE FIELDS ----------------
    @Column(nullable = false)
    private boolean deleted;

    private LocalDateTime deletedAt;

    private String deletedBy;

    // ---------------- ESCALATION FIELDS ----------------
    @Builder.Default
    @Column(name = "escalation_level")
    private Integer escalationLevel = 0;

    @Column(name = "last_escalation_time")
    private LocalDateTime lastEscalationTime;

    // ---------------- AUTO TIMESTAMPS ----------------
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.breached = false;
        this.deleted = false;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}