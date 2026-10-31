package com.org.uptime_monitor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> validationErrors;

    public ErrorResponse(String error, String message) {
        this.status = "error";
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String error, String message, Map<String, String> validationErrors) {
        this(error, message);
        this.validationErrors = validationErrors;
    }

    public String getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Map<String, String> getValidationErrors() { return validationErrors; }
}
