package com.org.uptime_monitor.controller;

import com.org.uptime_monitor.dto.ApiResponse;
import com.org.uptime_monitor.dto.EndpointRequestDTO;
import com.org.uptime_monitor.dto.EndpointResponseDTO;
import com.org.uptime_monitor.dto.UserEndpointsSummaryDTO;
import com.org.uptime_monitor.entity.Endpoint;
import com.org.uptime_monitor.service.EndpointService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/endpoints")
public class EndpointController {

    private final EndpointService endpointService;

    public EndpointController(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EndpointResponseDTO>> registerEndpoint(@Valid @RequestBody EndpointRequestDTO request) {
        Endpoint savedEndpoint = endpointService.registerEndpoint(request);
        EndpointResponseDTO responseDTO = EndpointResponseDTO.from(savedEndpoint);
        return ResponseEntity.ok(ApiResponse.success(responseDTO, "Endpoint registered successfully"));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> getStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(endpointService.getEndpointStatus(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserEndpointsSummaryDTO>> searchByEmail(@RequestParam String email) {
        UserEndpointsSummaryDTO summary = endpointService.getEndpointsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEndpoint(@PathVariable Long id) {
        endpointService.deleteEndpoint(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Endpoint deleted successfully"));
    }
}
