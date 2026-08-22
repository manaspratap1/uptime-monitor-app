package com.org.uptime_monitor.scheduler;

import com.org.uptime_monitor.dto.TaskMessageDTO;
import com.org.uptime_monitor.entity.Endpoint;
import com.org.uptime_monitor.repository.EndpointRepository;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UptimeScheduler {

    private final EndpointRepository endpointRepository;
    private final SqsTemplate sqsTemplate;

    private static final String QUEUE_NAME = "uptime-check-queue";

    public UptimeScheduler(EndpointRepository endpointRepository, SqsTemplate sqsTemplate) {
        this.endpointRepository = endpointRepository;
        this.sqsTemplate = sqsTemplate;
    }

    @Scheduled(fixedRate = 60000)
    public void dispatchChecks() {
        System.out.println("Cron triggered: Fetching endpoints from DB...");

        List<Endpoint> endpoints = endpointRepository.findAll();

        for (Endpoint endpoint : endpoints) {
            TaskMessageDTO task = new TaskMessageDTO(endpoint.getId(), endpoint.getUrl());

            sqsTemplate.send(to -> to
                    .queue(QUEUE_NAME)
                    .payload(task)
            );

            System.out.println("Queued check for: " + endpoint.getUrl());
        }
    }
}
