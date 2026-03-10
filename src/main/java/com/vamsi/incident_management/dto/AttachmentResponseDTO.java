package com.vamsi.incident_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentResponseDTO {

    private Long id;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private LocalDateTime uploadedAt;

}