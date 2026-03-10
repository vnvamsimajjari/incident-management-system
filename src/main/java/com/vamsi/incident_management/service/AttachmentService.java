package com.vamsi.incident_management.service;

import com.vamsi.incident_management.entity.IncidentAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {

    IncidentAttachment uploadFile(Long incidentId, MultipartFile file);

    List<IncidentAttachment> getAttachments(Long incidentId);

    void deleteAttachment(Long attachmentId);
}