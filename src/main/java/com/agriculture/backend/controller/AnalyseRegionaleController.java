package com.agriculture.backend.controller;

import com.agriculture.backend.dto.AnalyseRegionaleDTO;
import com.agriculture.backend.service.AnalyseRegionaleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/regions/analyse")
public class AnalyseRegionaleController {

    private final AnalyseRegionaleService service;

    public AnalyseRegionaleController(AnalyseRegionaleService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public AnalyseRegionaleDTO getAnalyse() {
        return service.getAnalyse();
    }
}