package com.vamsi.incident_management.service.impl;

import com.vamsi.incident_management.entity.Incident;
import com.vamsi.incident_management.entity.IncidentAttachment;
import com.vamsi.incident_management.repository.IncidentAttachmentRepository;
import com.vamsi.incident_management.repository.IncidentRepository;
import com.vamsi.incident_management.service.AttachmentService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final IncidentAttachmentRepository attachmentRepository;
    private final IncidentRepository incidentRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    @Override
    public IncidentAttachment uploadFile(Long incidentId, MultipartFile file) {

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty or missing");
        }

        // Find incident
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        try {

            // Create uploads folder if not exists
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Generate safe filename
            String originalName = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + "_" + originalName;

            // Save file to disk
            File destination = new File(uploadDir + fileName);
            file.transferTo(destination);

            // Save attachment in database
            IncidentAttachment attachment = IncidentAttachment.builder()
                    .fileName(originalName)
                    .fileType(file.getContentType())
                    .filePath(destination.getAbsolutePath())
                    .fileSize(file.getSize())
                    .uploadedAt(LocalDateTime.now())
                    .incident(incident)
                    .build();

            return attachmentRepository.save(attachment);

        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    @Override
    public List<IncidentAttachment> getAttachments(Long incidentId) {

        // Correct method name based on entity relationship
        return attachmentRepository.findByIncident_Id(incidentId);

    }

    @Override
    public void deleteAttachment(Long attachmentId) {

        attachmentRepository.deleteById(attachmentId);

    }
}