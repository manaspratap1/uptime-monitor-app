package com.org.uptime_monitor.dto;

public class EndpointRequestDTO {
    private String url;
    private String alertEmail;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getAlertEmail() { return alertEmail; }
    public void setAlertEmail(String alertEmail) { this.alertEmail = alertEmail; }
}
