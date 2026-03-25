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
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class IncidentCommentController {

    @Autowired
    private IncidentCommentService commentService;

    // ================= ADD COMMENT =================
    @PostMapping("/incident/{incidentId}")
    public ResponseEntity<?> addComment(
            @PathVariable Long incidentId,
            @Valid @RequestBody CommentRequest request) {

        IncidentComment savedComment =
                commentService.addComment(incidentId, request, "admin");

        return ResponseEntity.ok(
                Map.of(
                        "status", 200,
                        "message", "Comment added successfully",
                        "id", savedComment.getId(),
                        "createdAt", savedComment.getCreatedAt()
                )
        );
    }

    // ================= GET COMMENTS =================
    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByIncident(
            @PathVariable Long incidentId) {

        List<CommentResponse> comments =
                commentService.getCommentsByIncident(incidentId);

        return ResponseEntity.ok(comments);
    }

    // ================= EXCEPTION =================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("status", 400, "message", ex.getMessage())
        );
    }
}