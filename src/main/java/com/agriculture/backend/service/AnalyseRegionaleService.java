package com.agriculture.backend.service;

import com.agriculture.backend.dto.AnalyseRegionaleDTO;
import com.agriculture.backend.dto.RegionAnalyseDTO;
import com.agriculture.backend.repository.AnalyseRegionaleRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyseRegionaleService {

    private final AnalyseRegionaleRepository repository;

    public AnalyseRegionaleService(AnalyseRegionaleRepository repository) {
        this.repository = repository;
    }

    public AnalyseRegionaleDTO getAnalyse() {
        Map<String, RegionAnalyseDTO> parRegion = new LinkedHashMap<>();

        List<Object[]> agriculteursParRegion = repository.getAgriculteursParRegion();
        for (Object[] ligne : agriculteursParRegion) {
            String nomRegion = (ligne[0] != null) ? ligne[0].toString() : "Région inconnue";
            RegionAnalyseDTO dto = parRegion.computeIfAbsent(nomRegion, RegionAnalyseDTO::nouveauVide);
            dto.setNomRegion(nomRegion);
            dto.setTotalAgriculteurs(((Number) ligne[1]).longValue());
            dto.setTotalAgriculteursActifs(((Number) ligne[2]).longValue());
        }

        List<Object[]> distributionsParRegion = repository.getDistributionsParRegion();
        for (Object[] ligne : distributionsParRegion) {
            String nomRegion = (ligne[0] != null) ? ligne[0].toString() : "Région inconnue";
            RegionAnalyseDTO dto = parRegion.computeIfAbsent(nomRegion, RegionAnalyseDTO::nouveauVide);
            dto.setNomRegion(nomRegion);
            dto.setNombreDistributions(((Number) ligne[1]).longValue());
            dto.setMontantDistribue(((Number) ligne[2]).doubleValue());
        }

        for (RegionAnalyseDTO dto : parRegion.values()) {
            if (dto.getTotalAgriculteursActifs() > 0) {
                dto.setMontantMoyenParActif(dto.getMontantDistribue() / dto.getTotalAgriculteursActifs());
            } else {
                dto.setMontantMoyenParActif(0.0);
            }
        }

        List<RegionAnalyseDTO> regions = parRegion.values().stream()
                .sorted((a, b) -> Double.compare(b.getMontantDistribue(), a.getMontantDistribue()))
                .toList();

        AnalyseRegionaleDTO resultat = new AnalyseRegionaleDTO();
        resultat.setRegions(regions);

        regions.stream()
                .max((a, b) -> Long.compare(a.getTotalAgriculteursActifs(), b.getTotalAgriculteursActifs()))
                .ifPresent(r -> resultat.setRegionPlusActifs(r.getNomRegion()));

        regions.stream()
                .max((a, b) -> Long.compare(a.getNombreDistributions(), b.getNombreDistributions()))
                .ifPresent(r -> resultat.setRegionPlusDistributions(r.getNomRegion()));

        double montantTotal = regions.stream().mapToDouble(RegionAnalyseDTO::getMontantDistribue).sum();
        resultat.setMontantTotalDistribue(montantTotal);

        return resultat;
    }
}