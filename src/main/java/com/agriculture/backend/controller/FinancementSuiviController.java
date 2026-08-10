package com.agriculture.backend.controller;

import com.agriculture.backend.dto.SuiviFinancementsDTO;
import com.agriculture.backend.service.FinancementSuiviService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/financements/suivi")
public class FinancementSuiviController {

    private final FinancementSuiviService service;

    public FinancementSuiviController(FinancementSuiviService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public SuiviFinancementsDTO getSuivi() {
        return service.getSuivi();
    }
}