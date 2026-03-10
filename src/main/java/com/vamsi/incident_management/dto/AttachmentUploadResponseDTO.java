package com.vamsi.incident_management.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentUploadResponseDTO {

    private Long attachmentId;

    private String message;

}