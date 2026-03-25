package com.vamsi.incident_management.service;

import com.vamsi.incident_management.dto.CommentRequest;
import com.vamsi.incident_management.dto.CommentResponse;
import com.vamsi.incident_management.entity.Incident;
import com.vamsi.incident_management.entity.IncidentComment;
import com.vamsi.incident_management.entity.User;
import com.vamsi.incident_management.exception.ResourceNotFoundException;
import com.vamsi.incident_management.repository.IncidentCommentRepository;
import com.vamsi.incident_management.repository.IncidentRepository;
import com.vamsi.incident_management.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentCommentService {

    private final IncidentRepository incidentRepository;
    private final IncidentCommentRepository commentRepository;
    private final UserRepository userRepository;

    // ================= ADD COMMENT =================
    public IncidentComment addComment(Long incidentId, CommentRequest request, String username) {

        if (incidentId == null) {
            throw new IllegalArgumentException("Incident ID cannot be null");
        }

        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment message cannot be empty");
        }

        // Fetch Incident
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident", "id", incidentId));

        // Fetch User (from logged-in username)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Create Comment
        IncidentComment comment = IncidentComment.builder()
                .incident(incident)
                .user(user)
                .message(request.getMessage().trim())
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

    // ================= GET COMMENTS =================
    public List<CommentResponse> getCommentsByIncident(Long incidentId) {

        if (incidentId == null) {
            throw new IllegalArgumentException("Incident ID cannot be null");
        }

        return commentRepository.findByIncidentIdOrderByCreatedAtDesc(incidentId)
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