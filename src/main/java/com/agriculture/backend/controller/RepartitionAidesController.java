package com.agriculture.backend.controller;

import com.agriculture.backend.dto.RepartitionAidesDTO;
import com.agriculture.backend.service.RepartitionAidesService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aides/repartition")
public class RepartitionAidesController {

    private final RepartitionAidesService repartitionAidesService;

    public RepartitionAidesController(RepartitionAidesService repartitionAidesService) {
        this.repartitionAidesService = repartitionAidesService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public RepartitionAidesDTO getRepartition() {
        return repartitionAidesService.getRepartition();
    }
}