package com.org.uptime_monitor.service;

import com.org.uptime_monitor.dto.EndpointRequestDTO;
import com.org.uptime_monitor.dto.EndpointResponseDTO;
import com.org.uptime_monitor.dto.UserEndpointsSummaryDTO;
import com.org.uptime_monitor.entity.Endpoint;
import com.org.uptime_monitor.exception.EndpointAlreadyExistsException;
import com.org.uptime_monitor.exception.EndpointNotFoundException;
import com.org.uptime_monitor.repository.EndpointRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
                    "This URL is already being monitored"
            );
        }

        Endpoint newEndpoint = new Endpoint(request.getUrl(), request.getAlertEmail());

        return endpointRepository.save(newEndpoint);
    }

    public String getEndpointStatus(long id) {
        String cacheKey = "endpoint:" + id + ":status";

        String cachedStatus = redisTemplate.opsForValue().get(cacheKey);
        if (cachedStatus != null) {
            return cachedStatus;
        }

        Endpoint endpoint = endpointRepository.findById(id)
                .orElseThrow(() -> new EndpointNotFoundException("Endpoint with id " + id + " not found"));

        redisTemplate.opsForValue().set(cacheKey, endpoint.getStatus(), Duration.ofMinutes(5));
        return endpoint.getStatus();
    }

    public UserEndpointsSummaryDTO getEndpointsByEmail(String email) {
        List<Endpoint> endpoints = endpointRepository.findByAlertEmailIgnoreCase(email);
        List<EndpointResponseDTO> responseDTOs = endpoints.stream()
                .map(EndpointResponseDTO::from)
                .toList();
        return new UserEndpointsSummaryDTO(email, responseDTOs.size(), responseDTOs);
    }

    public void deleteEndpoint(Long id) {
        Endpoint endpoint = endpointRepository.findById(id)
                .orElseThrow(() -> new EndpointNotFoundException("Endpoint with id " + id + " not found"));

        String cacheKey = "endpoint:" + id + ":status";
        redisTemplate.delete(cacheKey);

        endpointRepository.delete(endpoint);
    }
}
