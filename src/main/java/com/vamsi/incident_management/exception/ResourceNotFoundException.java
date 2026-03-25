package com.vamsi.incident_management.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;
    private final HttpStatus status;

    // 🔹 Simple constructor
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
        this.status = HttpStatus.NOT_FOUND;
    }

    // 🔹 Detailed constructor (BEST PRACTICE)
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.status = HttpStatus.NOT_FOUND;
    }

    // 🔹 Getters (optional but useful for logging/debugging)
    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public HttpStatus getStatus() {
        return status;
    }
}