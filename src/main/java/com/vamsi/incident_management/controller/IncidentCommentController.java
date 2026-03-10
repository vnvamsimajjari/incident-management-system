package com.vamsi.incident_management.controller;

import com.vamsi.incident_management.dto.CommentRequest;
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

    // ✅ FIXED: Get comments by incident
    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<List<IncidentComment>> getCommentsByIncident(@PathVariable Long incidentId) {
        List<IncidentComment> comments = commentService.getCommentsByIncident(incidentId);
        return ResponseEntity.ok(comments);
    }
}