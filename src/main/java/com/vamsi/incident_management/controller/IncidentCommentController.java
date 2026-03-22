package com.vamsi.incident_management.controller;

import com.vamsi.incident_management.dto.CommentRequest;
import com.vamsi.incident_management.dto.CommentResponse;
import com.vamsi.incident_management.entity.IncidentComment;
import com.vamsi.incident_management.service.IncidentCommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class IncidentCommentController {

    @Autowired
    private IncidentCommentService commentService;

    // ✅ Add Comment API
    @PostMapping
    public ResponseEntity<IncidentComment> addComment(@Valid @RequestBody CommentRequest request) {
        IncidentComment savedComment = commentService.addComment(request);
        return ResponseEntity.ok(savedComment);
    }

    // ✅ Get Comments by Incident (DTO response)
    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByIncident(@PathVariable Long incidentId) {

        if (incidentId == null) {
            return ResponseEntity.badRequest().build(); // small safety check
        }

        List<CommentResponse> comments = commentService.getCommentsByIncident(incidentId);

        return ResponseEntity.ok(comments);
    }
}