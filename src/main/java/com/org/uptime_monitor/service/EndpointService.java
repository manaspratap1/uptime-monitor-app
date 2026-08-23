package com.org.uptime_monitor.service;

import com.org.uptime_monitor.dto.EndpointRequestDTO;
import com.org.uptime_monitor.dto.UserEndpointsSummaryDTO;
import com.org.uptime_monitor.entity.Endpoint;
import com.org.uptime_monitor.exception.EndpointAlreadyExistsException;
import com.org.uptime_monitor.repository.EndpointRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.endpoints.internal.Value;

import java.time.Duration;
import java.util.List;

@Service
public class EndpointService {

    private final EndpointRepository endpointRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public EndpointService(EndpointRepository endpointRepository, RedisTemplate<String, String> redisTemplate) {
        this.endpointRepository = endpointRepository;
        this.redisTemplate = redisTemplate;
    }

    public Endpoint registerEndpoint(EndpointRequestDTO request) {

        if (endpointRepository.existsByUrl(request.getUrl())) {
            throw new EndpointAlreadyExistsException(
                    "This URL is already being monitored for the email: " + request.getAlertEmail()
            );
        }

        Endpoint newEndpoint = new Endpoint(request.getUrl(), request.getAlertEmail());

        return endpointRepository.save(newEndpoint);
    }

    public String getEndpointStatus(long id){
        String cacheKey = "endpoint:" + id + ":status";

        String cachedStatus = redisTemplate.opsForValue().get(cacheKey);
        if (cachedStatus != null) {
            System.out.println("Cache HIT for endpoint ID: " + id);
            return cachedStatus;
        }

        Endpoint endpoint = endpointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endpoint not found"));

        redisTemplate.opsForValue().set(cacheKey, endpoint.getStatus(), Duration.ofMinutes(5));
        return endpoint.getStatus();
    }

    public UserEndpointsSummaryDTO getEndpointsByEmail(String email) {
        List<Endpoint> endpoints = endpointRepository.findByAlertEmailIgnoreCase(email);
        return new UserEndpointsSummaryDTO(email, endpoints.size(), endpoints);
    }
}
