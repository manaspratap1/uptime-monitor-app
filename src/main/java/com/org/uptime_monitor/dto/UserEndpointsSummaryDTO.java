package com.org.uptime_monitor.dto;

import java.util.List;

public class UserEndpointsSummaryDTO {

    private String alertEmail;
    private int totalMonitored;
    private List<EndpointResponseDTO> endpoints;

    public UserEndpointsSummaryDTO(String alertEmail, int totalMonitored, List<EndpointResponseDTO> endpoints) {
        this.alertEmail = alertEmail;
        this.totalMonitored = totalMonitored;
        this.endpoints = endpoints;
    }

    // Getters and Setters
    public String getAlertEmail() { return alertEmail; }
    public void setAlertEmail(String alertEmail) { this.alertEmail = alertEmail; }
    public int getTotalMonitored() { return totalMonitored; }
    public void setTotalMonitored(int totalMonitored) { this.totalMonitored = totalMonitored; }
    public List<EndpointResponseDTO> getEndpoints() { return endpoints; }
    public void setEndpoints(List<EndpointResponseDTO> endpoints) { this.endpoints = endpoints; }
}
