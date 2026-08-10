package com.agriculture.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.agriculture.backend.model.Region;
import java.util.List;

@Repository
public interface AnalyseRegionaleRepository extends JpaRepository<Region, Long> {

    @Query(value = "SELECT r.nom AS nom_region, " +
                   "COUNT(a.id) AS total_agriculteurs, " +
                   "COUNT(CASE WHEN a.actif THEN 1 END) AS total_actifs " +
                   "FROM {h-schema}regions r " +
                   "LEFT JOIN {h-schema}districts d ON d.region_id = r.id " +
                   "LEFT JOIN {h-schema}agriculteurs a ON a.district_id = d.id " +
                   "GROUP BY r.nom " +
                   "ORDER BY r.nom",
                   nativeQuery = true)
    List<Object[]> getAgriculteursParRegion();

    @Query(value = "SELECT r.nom AS nom_region, " +
                   "COUNT(dist.id) AS nombre_distributions, " +
                   "COALESCE(SUM(dist.montant), 0.0) AS montant_distribue " +
                   "FROM {h-schema}regions r " +
                   "LEFT JOIN {h-schema}districts d ON d.region_id = r.id " +
                   "LEFT JOIN {h-schema}agriculteurs a ON a.district_id = d.id " +
                   "LEFT JOIN {h-schema}demandes_aides da ON da.agriculteur_id = a.id " +
                   "LEFT JOIN {h-schema}distributions_aides dist ON dist.demande_id = da.id " +
                   "GROUP BY r.nom " +
                   "ORDER BY r.nom",
                   nativeQuery = true)
    List<Object[]> getDistributionsParRegion();
}