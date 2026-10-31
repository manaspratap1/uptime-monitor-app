package com.org.uptime_monitor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.org.uptime_monitor.entity.Endpoint;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EndpointResponseDTO {

    private Long id;
    private String url;
    private String alertEmail;
    private String status;
    private LocalDateTime lastChecked;

    public static EndpointResponseDTO from(Endpoint endpoint) {
        EndpointResponseDTO dto = new EndpointResponseDTO();
        dto.id = endpoint.getId();
        dto.url = endpoint.getUrl();
        dto.alertEmail = endpoint.getAlertEmail();
        dto.status = endpoint.getStatus();
        dto.lastChecked = endpoint.getLastChecked();
        return dto;
    }

    public Long getId() { return id; }
    public String getUrl() { return url; }
    public String getAlertEmail() { return alertEmail; }
    public String getStatus() { return status; }
    public LocalDateTime getLastChecked() { return lastChecked; }
}
