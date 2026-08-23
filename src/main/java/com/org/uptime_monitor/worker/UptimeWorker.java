package com.org.uptime_monitor.worker;

import com.org.uptime_monitor.dto.TaskMessageDTO;
import com.org.uptime_monitor.entity.Endpoint;
import com.org.uptime_monitor.repository.EndpointRepository;
import io.awspring.cloud.sns.core.SnsTemplate;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class UptimeWorker {

    private final EndpointRepository endpointRepository;
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final SnsTemplate snsTemplate;

    private static final String SNS_TOPIC_ARN = "arn:aws:sns:ap-southeast-2:284443069026:uptime-alerts";

    public UptimeWorker(EndpointRepository endpointRepository,
                        RestTemplate restTemplate,
                        RedisTemplate<String, String> redisTemplate,
                        SnsTemplate snsTemplate) {
        this.endpointRepository = endpointRepository;
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
        this.snsTemplate = snsTemplate;
    }

    @SqsListener("uptime-check-queue")
    public void processCheck(TaskMessageDTO task) {
        Endpoint endpoint = endpointRepository.findById(task.getEndpointId())
                .orElseThrow(() -> new RuntimeException("Endpoint not found in DB"));

        String newStatus;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(task.getUrl(), String.class);
            newStatus = response.getStatusCode().is2xxSuccessful() ? "UP" : "DOWN";
        } catch (RestClientException e) {
            newStatus = "DOWN";
        }

        // --- NEW COOLDOWN & STATE TRANSITION LOGIC ---
        boolean shouldAlert = false;

        if ("DOWN".equals(newStatus)) {
            if (!"DOWN".equals(endpoint.getStatus())) {
                // Case 1: Website just went down from UP/PENDING
                shouldAlert = true;
            } else if (endpoint.getLastAlertSentAt() != null) {
                // Case 2: Already down, check if cooldown period has passed
                long minutesSinceLastAlert = ChronoUnit.MINUTES.between(
                        endpoint.getLastAlertSentAt(),
                        LocalDateTime.now()
                );

                int interval = endpoint.getAlertIntervalMinutes() != null ? endpoint.getAlertIntervalMinutes() : 180;
                if (minutesSinceLastAlert >= interval) {
                    shouldAlert = true;
                }
            } else {
                shouldAlert = true;
            }
        }

        if (shouldAlert) {
            String alertMessage = String.format("URGENT: Your monitored endpoint %s is currently DOWN! Alert sent to %s.",
                    task.getUrl(), endpoint.getAlertEmail());

            snsTemplate.convertAndSend(SNS_TOPIC_ARN, alertMessage);
            System.out.println("🚨 AWS SNS Alert Published for: " + task.getUrl());

            // Update the last alert timestamp
            endpoint.setLastAlertSentAt(LocalDateTime.now());
        }
        // ---------------------------------------------

        endpoint.setStatus(newStatus);
        endpoint.setLastChecked(LocalDateTime.now());
        endpointRepository.save(endpoint);

        String cacheKey = "endpoint:" + endpoint.getId() + ":status";
        redisTemplate.opsForValue().set(cacheKey, newStatus, Duration.ofMinutes(5));

        System.out.println("Status updated to " + newStatus + " for " + task.getUrl());
    }
}