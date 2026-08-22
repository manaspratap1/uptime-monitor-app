package com.org.uptime_monitor.service;

import com.org.uptime_monitor.dto.EndpointRequestDTO;
import com.org.uptime_monitor.entity.Endpoint;
import com.org.uptime_monitor.repository.EndpointRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.endpoints.internal.Value;

import java.time.Duration;

@Service
public class EndpointService {

    private final EndpointRepository endpointRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public EndpointService(EndpointRepository endpointRepository, RedisTemplate<String, String> redisTemplate) {
        this.endpointRepository = endpointRepository;
        this.redisTemplate = redisTemplate;
    }

    public Endpoint registerEndpoint(EndpointRequestDTO request) {
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

        System.out.println("Cache MISS for endpoint ID: " + id + ". Querying DB...");
        Endpoint endpoint = endpointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endpoint not found"));

        redisTemplate.opsForValue().set(cacheKey, endpoint.getStatus(), Duration.ofMinutes(5));
        return endpoint.getStatus();
    }
}
