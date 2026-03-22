package com.vamsi.incident_management.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private String username; // ✅ include who commented
}