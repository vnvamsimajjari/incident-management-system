package com.vamsi.incident_management.service;

import com.vamsi.incident_management.dto.CommentRequest;
import com.vamsi.incident_management.dto.CommentResponse;
import com.vamsi.incident_management.entity.Incident;
import com.vamsi.incident_management.entity.IncidentComment;
import com.vamsi.incident_management.entity.User;
import com.vamsi.incident_management.repository.IncidentRepository;
import com.vamsi.incident_management.repository.IncidentCommentRepository;
import com.vamsi.incident_management.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentCommentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private IncidentCommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ ADD COMMENT
    public IncidentComment addComment(CommentRequest request) {

        if (request.getIncidentId() == null) {
            throw new RuntimeException("Incident ID cannot be null");
        }

        if (request.getComment() == null || request.getComment().isBlank()) {
            throw new RuntimeException("Comment cannot be empty");
        }

        // Fetch incident
        Incident incident = incidentRepository.findById(request.getIncidentId())
                .orElseThrow(() -> new RuntimeException("Incident not found with ID: " + request.getIncidentId()));

        // Temporary user (replace with JWT later)
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create comment
        IncidentComment comment = new IncidentComment();
        comment.setIncident(incident);
        comment.setUser(user);
        comment.setMessage(request.getComment());

        return commentRepository.save(comment);
    }

    // ✅ GET COMMENTS BY INCIDENT (DTO response)
    public List<CommentResponse> getCommentsByIncident(Long incidentId) {

        if (incidentId == null) {
            throw new RuntimeException("Incident ID cannot be null");
        }

        return commentRepository.findByIncidentIdOrderByCreatedAtAsc(incidentId)
                .stream()
                .map(comment -> CommentResponse.builder()
                        .id(comment.getId())
                        .message(comment.getMessage())
                        .createdAt(comment.getCreatedAt())
                        .username(
                                comment.getUser() != null
                                        ? comment.getUser().getUsername()
                                        : "Unknown"
                        )
                        .build()
                )
                .toList();
    }
}