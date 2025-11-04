package com.countyhospital.healthapi.common.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API error response structure")
public class ApiErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Timestamp when the error occurred", example = "2025-11-15 10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "HTTP status description", example = "Bad Request")
    private String error;

    @Schema(description = "Detailed error message", example = "Validation failed")
    private String message;

    @Schema(description = "API path where the error occurred", example = "/api/patients")
    private String path;

    @Schema(description = "List of field-specific validation errors")
    private List<ValidationError> validationErrors;

    @Schema(description = "Debug message for developers (only in non-production)")
    private String debugMessage;

    // Constructors
    public ApiErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiErrorResponse(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ApiErrorResponse(int status, String error, String message, String path, String debugMessage) {
        this(status, error, message, path);
        this.debugMessage = debugMessage;
    }

    // Builder pattern 
    public static ApiErrorResponseBuilder builder() {
        return new ApiErrorResponseBuilder();
    }

    // Getters and setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public List<ValidationError> getValidationErrors() { return validationErrors; }
    public void setValidationErrors(List<ValidationError> validationErrors) { this.validationErrors = validationErrors; }

    public String getDebugMessage() { return debugMessage; }
    public void setDebugMessage(String debugMessage) { this.debugMessage = debugMessage; }

    // Validation error
    public void addValidationError(String field, String message) {
        if (this.validationErrors == null) {
            this.validationErrors = new ArrayList<>();
        }
        this.validationErrors.add(new ValidationError(field, message));
    }

    // Validation error inner class
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ValidationError {
        @Schema(description = "Field name that failed validation", example = "email")
        private final String field;

        @Schema(description = "Validation error message", example = "Email must be valid")
        private final String message;

        @Schema(description = "Rejected value", example = "invalid-email")
        private final Object rejectedValue;

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
            this.rejectedValue = null;
        }

        public ValidationError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public String getField() { return field; }
        public String getMessage() { return message; }
        public Object getRejectedValue() { return rejectedValue; }
    }

    // Builder class
    public static class ApiErrorResponseBuilder {
        private int status;
        private String error;
        private String message;
        private String path;
        private String debugMessage;
        private List<ValidationError> validationErrors;

        public ApiErrorResponseBuilder status(int status) {
            this.status = status;
            return this;
        }

        public ApiErrorResponseBuilder error(String error) {
            this.error = error;
            return this;
        }

        public ApiErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ApiErrorResponseBuilder path(String path) {
            this.path = path;
            return this;
        }

        public ApiErrorResponseBuilder debugMessage(String debugMessage) {
            this.debugMessage = debugMessage;
            return this;
        }

        public ApiErrorResponseBuilder validationErrors(List<ValidationError> validationErrors) {
            this.validationErrors = validationErrors;
            return this;
        }

        public ApiErrorResponse build() {
            ApiErrorResponse response = new ApiErrorResponse(status, error, message, path, debugMessage);
            if (validationErrors != null) {
                response.setValidationErrors(validationErrors);
            }
            return response;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiErrorResponse that = (ApiErrorResponse) o;
        return status == that.status && 
               Objects.equals(timestamp, that.timestamp) && 
               Objects.equals(error, that.error) && 
               Objects.equals(message, that.message) && 
               Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, status, error, message, path);
    }

    @Override
    public String toString() {
        return "ApiErrorResponse{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", validationErrors=" + validationErrors +
                '}';
    }
}