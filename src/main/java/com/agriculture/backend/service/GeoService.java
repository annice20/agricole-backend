package com.agriculture.backend.service;

import com.agriculture.backend.dto.GeoAgriculteurDTO;
import java.util.List;
import java.util.Map;

public interface GeoService {
    List<GeoAgriculteurDTO> getTousAvecCoordonnees();
    List<GeoAgriculteurDTO> getBeneficiairesAvecCoordonnees();
    void updateGeolocalisation(Long id, Double latitude, Double longitude);
    List<Map<String, Object>> getStatistiquesParRegion();
}