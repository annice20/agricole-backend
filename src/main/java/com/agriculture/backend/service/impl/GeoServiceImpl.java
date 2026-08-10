package com.agriculture.backend.service.impl;

import com.agriculture.backend.dto.GeoAgriculteurDTO;
import com.agriculture.backend.repository.AgriculteurRepository;
import com.agriculture.backend.service.GeoService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeoServiceImpl implements GeoService {

    private final AgriculteurRepository agriculteurRepository;

    public GeoServiceImpl(AgriculteurRepository agriculteurRepository) {
        this.agriculteurRepository = agriculteurRepository;
    }

    // Conversion Object[] → GeoAgriculteurDTO
    private GeoAgriculteurDTO convertir(Object[] ligne) {
        return new GeoAgriculteurDTO(
            ligne[0] != null ? ((Number) ligne[0]).longValue() : null,
            ligne[1] != null ? ligne[1].toString() : "",
            ligne[2] != null ? ligne[2].toString() : "",
            ligne[3] != null ? ligne[3].toString() : "",
            ligne[4] != null ? ((Number) ligne[4]).doubleValue() : null,
            ligne[5] != null ? ligne[5].toString() : "",
            ligne[6] != null && Boolean.parseBoolean(ligne[6].toString()),
            ligne[7] != null ? ((Number) ligne[7]).doubleValue() : null,
            ligne[8] != null ? ((Number) ligne[8]).doubleValue() : null,
            ligne[9] != null ? ligne[9].toString() : "",
            ligne[10] != null ? ligne[10].toString() : ""
        );
    }

    @Override
    public List<GeoAgriculteurDTO> getTousAvecCoordonnees() {
        List<GeoAgriculteurDTO> result = new ArrayList<>();
        for (Object[] ligne : agriculteurRepository.findAllAvecCoordonnees()) {
            result.add(convertir(ligne));
        }
        return result;
    }

    @Override
    public List<GeoAgriculteurDTO> getBeneficiairesAvecCoordonnees() {
        List<GeoAgriculteurDTO> result = new ArrayList<>();
        for (Object[] ligne : agriculteurRepository.findBeneficiairesAvecCoordonnees()) {
            result.add(convertir(ligne));
        }
        return result;
    }

    @Override
    public void updateGeolocalisation(Long id, Double latitude, Double longitude) {
        agriculteurRepository.updateGeolocalisation(id, latitude, longitude);
    }

    @Override
    public List<Map<String, Object>> getStatistiquesParRegion() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] ligne : agriculteurRepository.getStatistiquesParRegion()) {
            Map<String, Object> map = new HashMap<>();
            map.put("nomRegion", ligne[0] != null ? ligne[0].toString() : "");
            map.put("total", ligne[1] != null ? ((Number) ligne[1]).longValue() : 0);
            map.put("totalActifs", ligne[2] != null ? ((Number) ligne[2]).longValue() : 0);
            map.put("superficieTotale", ligne[3] != null ? ((Number) ligne[3]).doubleValue() : 0.0);
            map.put("totalLocalises", ligne[4] != null ? ((Number) ligne[4]).longValue() : 0);
            result.add(map);
        }
        return result;
    }
}