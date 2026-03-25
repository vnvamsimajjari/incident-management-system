package com.vamsi.incident_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "Comment message is required")
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String message;
}