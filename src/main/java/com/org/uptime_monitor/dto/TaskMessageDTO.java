package com.org.uptime_monitor.dto;

public class TaskMessageDTO {

    private Long endpointId;
    private String url;

    public TaskMessageDTO () {}

    public TaskMessageDTO (Long endpointId, String url) {
        this.endpointId = endpointId;
        this.url = url;
    }

    public Long getEndpointId() { return endpointId; }
    public void setEndpointId(Long endpointId) { this.endpointId = endpointId; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
