package com.vamsi.incident_management.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ================= 404 =================
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ApiError error = buildError(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage(),
                request);

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // ================= 403 =================
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        ApiError error = buildError(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                ex.getMessage(), // ✅ FIXED
                request);

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // ================= 400 VALIDATION =================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ApiError error = buildError(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                message,
                request);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // ================= 400 BUSINESS RULE =================
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request) {

        ApiError error = buildError(
                HttpStatus.BAD_REQUEST,
                "Invalid State",
                ex.getMessage(),
                request);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // ================= 400 BAD INPUT =================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        ApiError error = buildError(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage(),
                request);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // ================= 500 =================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        // 🔥 Replace with logger in future
        System.err.println("Unhandled Exception: " + ex.getMessage());

        ApiError error = buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Something went wrong",
                request);

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ================= COMMON BUILDER =================
    private ApiError buildError(HttpStatus status, String error,
                                String message, HttpServletRequest request) {

        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .build();
    }
}