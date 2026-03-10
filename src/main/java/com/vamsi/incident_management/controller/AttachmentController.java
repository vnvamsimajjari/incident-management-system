package com.vamsi.incident_management.controller;

import com.vamsi.incident_management.entity.IncidentAttachment;
import com.vamsi.incident_management.service.AttachmentService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/{incidentId}")
    public IncidentAttachment uploadAttachment(
            @PathVariable Long incidentId,
            @RequestParam("file") MultipartFile file
    ) {
        return attachmentService.uploadFile(incidentId, file);
    }

    @GetMapping("/{incidentId}")
    public List<IncidentAttachment> getAttachments(
            @PathVariable Long incidentId
    ) {
        return attachmentService.getAttachments(incidentId);
    }

    @DeleteMapping("/{id}")
    public void deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
    }
}