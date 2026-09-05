package com.agriculture.backend.controller;

import com.agriculture.backend.model.RoleName;
import com.agriculture.backend.model.Utilisateur;
import com.agriculture.backend.service.RapportRegionalService;
import com.agriculture.backend.service.UtilisateurService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/rapports/regional")
public class RapportRegionalController {

    private final RapportRegionalService service;
    private final UtilisateurService utilisateurService;

    public RapportRegionalController(RapportRegionalService service,
                                      UtilisateurService utilisateurService) {
        this.service = service;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/pdf")
    @PreAuthorize("hasRole('RESPONSABLE_REGIONAL') or hasRole('ADMIN_NATIONAL')")
    public ResponseEntity<byte[]> telechargerPdf(Authentication authentication) {
        Utilisateur user = utilisateurService.getByEmail(authentication.getName());

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getNom() == RoleName.ADMIN_NATIONAL);

        if (!isAdmin && user.getRegionGeree() == null) {
            return ResponseEntity.status(403).build();
        }

        Long regionId = isAdmin ? null : user.getRegionGeree().getId();
        String nomRegion = isAdmin ? "Toutes régions" : user.getRegionGeree().getNom();

        byte[] pdf = service.genererRapportPdf(regionId, nomRegion);
        String nomFichier = "rapport-agricole-regional-" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}