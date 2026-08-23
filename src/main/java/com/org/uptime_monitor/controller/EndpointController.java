package com.org.uptime_monitor.controller;

import com.org.uptime_monitor.dto.EndpointRequestDTO;
import com.org.uptime_monitor.entity.Endpoint;
import com.org.uptime_monitor.service.EndpointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/endpoints")
@CrossOrigin(origins = "*")
public class EndpointController {

    private final EndpointService endpointService;

    public EndpointController(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    @PostMapping
    public ResponseEntity<Endpoint> registerEndpoint(@RequestBody EndpointRequestDTO request) {
        Endpoint savedEndpoint = endpointService.registerEndpoint(request);
        return ResponseEntity.ok(savedEndpoint);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getStatus(@PathVariable Long id) {
        return ResponseEntity.ok(endpointService.getEndpointStatus(id));
    }
}
