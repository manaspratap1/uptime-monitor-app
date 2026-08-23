package com.org.uptime_monitor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "endpoints")
public class Endpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String alertEmail;

    @Column(nullable = false)
    private String status;

    private LocalDateTime lastChecked;

    @Column(name = "last_alert_sent_at")
    private LocalDateTime lastAlertSentAt;

    @Column(name = "alert_interval_minutes")
    private Integer alertIntervalMinutes = 180;

    public Endpoint() {}

    public Endpoint(String url, String alertEmail) {
        this.url = url;
        this.alertEmail = alertEmail;
        this.status = "PENDING";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getAlertEmail() { return alertEmail; }
    public void setAlertEmail(String alertEmail) { this.alertEmail = alertEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLastChecked() { return lastChecked; }
    public void setLastChecked(LocalDateTime lastChecked) { this.lastChecked = lastChecked; }

    public LocalDateTime getLastAlertSentAt() {
        return lastAlertSentAt;
    }

    public Integer getAlertIntervalMinutes() {
        return alertIntervalMinutes;
    }

    public void setLastAlertSentAt(LocalDateTime lastAlertSentAt) {
        this.lastAlertSentAt = lastAlertSentAt;
    }

    public void setAlertIntervalMinutes(Integer alertIntervalMinutes) {
        this.alertIntervalMinutes = alertIntervalMinutes;
    }
}
