package com.agriculture.backend.controller;

import com.agriculture.backend.dto.GeoAgriculteurDTO;
import com.agriculture.backend.service.GeoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/geo")
public class GeoController {

    private final GeoService geoService;

    public GeoController(GeoService geoService) {
        this.geoService = geoService;
    }

    // Toutes les exploitations localisées
    @GetMapping("/exploitations")
    public List<GeoAgriculteurDTO> getTousAvecCoordonnees() {
        return geoService.getTousAvecCoordonnees();
    }

    // Bénéficiaires actifs uniquement
    @GetMapping("/beneficiaires")
    public List<GeoAgriculteurDTO> getBeneficiaires() {
        return geoService.getBeneficiairesAvecCoordonnees();
    }

    // Statistiques par région
    @GetMapping("/statistiques-region")
    public List<Map<String, Object>> getStatistiquesParRegion() {
        return geoService.getStatistiquesParRegion();
    }

    // Mise à jour GPS d'un agriculteur
    @PutMapping("/localiser/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void localiser(
            @PathVariable Long id,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        geoService.updateGeolocalisation(id, latitude, longitude);
    }
}