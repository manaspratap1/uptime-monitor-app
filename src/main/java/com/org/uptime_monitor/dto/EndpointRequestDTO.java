package com.org.uptime_monitor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class EndpointRequestDTO {

    @NotBlank(message = "URL is required")
    @Pattern(regexp = "^https?://.*", message = "Must be a valid HTTP or HTTPS URL")
    private String url;

    @NotBlank(message = "Alert email is required")
    @Email(message = "Must be a valid email address")
    private String alertEmail;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getAlertEmail() { return alertEmail; }
    public void setAlertEmail(String alertEmail) { this.alertEmail = alertEmail; }
}
