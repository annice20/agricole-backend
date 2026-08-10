package com.agriculture.backend.service.impl;

import org.springframework.stereotype.Service;
import com.agriculture.backend.dto.DashboardDTO;
import com.agriculture.backend.repository.DashboardRepository;
import com.agriculture.backend.service.DashboardService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final DashboardRepository repository;

    public DashboardServiceImpl(DashboardRepository repository) {
        this.repository = repository;
    }

    @Override
    public DashboardDTO getDashboard(Long regionId) {
        DashboardDTO dto = new DashboardDTO();

        boolean vueRegionale = (regionId != null);

        dto.setAgriculteurs(
            vueRegionale
                ? repository.countAgriculteursByRegion(regionId)
                : repository.countAgriculteurs()
        );
        dto.setAgriculteursTous(
            vueRegionale
                ? repository.countAgriculteursTousByRegion(regionId)
                : repository.countAgriculteursTous()
        );
        dto.setProgrammes(
            vueRegionale
                ? repository.countProgrammesByRegion(regionId)
                : repository.countProgrammes()
        );
        dto.setDistributions(
            vueRegionale
                ? repository.countDistributionsByRegion(regionId)
                : repository.countDistributions()
        );

        // Financements : toujours national, un financement n'est lié qu'à un
        // programme (potentiellement national), pas à une région précise.
        Double montant = repository.montantTotalFinancements();
        dto.setMontantTotalFinancement(montant != null ? montant : 0.0);

        dto.setRepartitionAides(new ArrayList<>());

        List<Map<String, Object>> listAnalyseRegionale = new ArrayList<>();
        try {
            List<Object[]> resultatsRequete = vueRegionale
                ? repository.getAgriculteursActifsParRegionUnique(regionId)
                : repository.getAgriculteursActifsParRegion();

            for (Object[] ligne : resultatsRequete) {
                Map<String, Object> mapRegion = new HashMap<>();
                String nomRegion = (ligne[0] != null) ? ligne[0].toString() : "Région inconnue";
                long totalActifs = (ligne[1] != null) ? ((Number) ligne[1]).longValue() : 0L;
                mapRegion.put("nomRegion", nomRegion);
                mapRegion.put("totalActifs", totalActifs);
                listAnalyseRegionale.add(mapRegion);
            }
        } catch (Exception e) {
            System.err.println("Erreur analyse régionale : " + e.getMessage());
        }
        dto.setAnalyseRegionale(listAnalyseRegionale);

        return dto;
    }
}