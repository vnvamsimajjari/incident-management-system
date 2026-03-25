package com.vamsi.incident_management.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentIncidentDTO {

    private Long id;
    private String title;
    private String status;
    private String priority;
    private LocalDateTime createdAt;
}