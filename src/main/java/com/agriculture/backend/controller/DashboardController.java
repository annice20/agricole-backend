package com.agriculture.backend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agriculture.backend.dto.DashboardDTO;
import com.agriculture.backend.model.RoleName;
import com.agriculture.backend.model.Utilisateur;
import com.agriculture.backend.service.DashboardService;
import com.agriculture.backend.service.UtilisateurService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
	
	private final DashboardService dashboardService;
	private final UtilisateurService utilisateurService;

    public DashboardController(DashboardService dashboardService, UtilisateurService utilisateurService) {
        this.dashboardService = dashboardService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public DashboardDTO dashboard(Authentication authentication) {
        Utilisateur user = utilisateurService.getByEmail(authentication.getName());
        boolean isAdmin = user.getRoles().stream()
            .anyMatch(r -> r.getNom() == RoleName.ADMIN_NATIONAL);
        Long regionId = isAdmin ? null : (user.getRegionGeree() != null ? user.getRegionGeree().getId() : null);
        return dashboardService.getDashboard(regionId);
    }
}
