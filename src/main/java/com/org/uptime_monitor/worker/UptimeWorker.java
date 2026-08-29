package com.org.uptime_monitor.worker;

import com.org.uptime_monitor.dto.TaskMessageDTO;
import com.org.uptime_monitor.entity.Endpoint;
import com.org.uptime_monitor.repository.EndpointRepository;
import com.org.uptime_monitor.service.EmailService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class UptimeWorker {

    private final EndpointRepository endpointRepository;
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;

    @SqsListener("uptime-check-queue")
    public void processCheck(TaskMessageDTO task) {
        Endpoint endpoint = endpointRepository.findById(task.getEndpointId())
                .orElse(null);

        if (endpoint == null) {
            log.warn("Skipping uptime check because endpoint {} no longer exists", task.getEndpointId());
            return;
        }

        String newStatus;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(task.getUrl(), String.class);
            newStatus = response.getStatusCode().is2xxSuccessful() ? "UP" : "DOWN";
        } catch (RestClientException e) {
            newStatus = "DOWN";
        }

        boolean shouldAlert = false;

        if ("DOWN".equals(newStatus)) {
            if (!"DOWN".equals(endpoint.getStatus())) {
                shouldAlert = true;
            } else if (endpoint.getLastAlertSentAt() != null) {
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
            LocalDateTime alertSentAt = LocalDateTime.now();

            emailService.sendAlertMail(
                    endpoint.getAlertEmail(),
                    "URGENT: Endpoint is DOWN",
                    "Your endpoint " + endpoint.getUrl() + " is currently DOWN."
            );

            endpoint.setLastAlertSentAt(alertSentAt);
        }

        endpoint.setStatus(newStatus);
        endpoint.setLastChecked(LocalDateTime.now());
        endpointRepository.save(endpoint);

        String cacheKey = "endpoint:" + endpoint.getId() + ":status";
        redisTemplate.opsForValue().set(cacheKey, newStatus, Duration.ofMinutes(5));

    }
}