package com.agriculture.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.agriculture.backend.model.Agriculteur;
import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Agriculteur, Long> {

    @Query(value = "SELECT COUNT(*) FROM {h-schema}agriculteurs WHERE actif = true", nativeQuery = true)
    long countAgriculteurs();

    @Query(value = "SELECT COUNT(*) FROM {h-schema}agriculteurs", nativeQuery = true)
    long countAgriculteursTous();

    @Query(value = "SELECT COUNT(*) FROM {h-schema}programmes_aides", nativeQuery = true)
    long countProgrammes();

    @Query(value = "SELECT COUNT(*) FROM {h-schema}distributions_aides", nativeQuery = true)
    long countDistributions();

    @Query(value = "SELECT COALESCE(SUM(montant), 0.0) FROM {h-schema}financements_agricoles", nativeQuery = true)
    Double montantTotalFinancements();

    @Query(value = "SELECT r.nom AS nom_region, COUNT(a.id) AS total_actifs " +
                   "FROM {h-schema}regions r " +
                   "LEFT JOIN {h-schema}districts d ON d.region_id = r.id " +
                   "LEFT JOIN {h-schema}agriculteurs a ON a.district_id = d.id AND a.actif = true " +
                   "GROUP BY r.nom " +
                   "ORDER BY total_actifs DESC",
                   nativeQuery = true)
    List<Object[]> getAgriculteursActifsParRegion();

    // ============================================================
    // Variantes filtrées par région (vue RESPONSABLE_REGIONAL)
    // ============================================================

    @Query(value = "SELECT COUNT(*) FROM {h-schema}agriculteurs a " +
                   "JOIN {h-schema}districts d ON a.district_id = d.id " +
                   "WHERE d.region_id = :regionId AND a.actif = true", nativeQuery = true)
    long countAgriculteursByRegion(@Param("regionId") Long regionId);

    @Query(value = "SELECT COUNT(*) FROM {h-schema}agriculteurs a " +
                   "JOIN {h-schema}districts d ON a.district_id = d.id " +
                   "WHERE d.region_id = :regionId", nativeQuery = true)
    long countAgriculteursTousByRegion(@Param("regionId") Long regionId);

    @Query(value = "SELECT COUNT(*) FROM {h-schema}distributions_aides dist " +
                   "JOIN {h-schema}demandes_aides dem ON dist.demande_id = dem.id " +
                   "JOIN {h-schema}agriculteurs a ON dem.agriculteur_id = a.id " +
                   "JOIN {h-schema}districts d ON a.district_id = d.id " +
                   "WHERE d.region_id = :regionId", nativeQuery = true)
    long countDistributionsByRegion(@Param("regionId") Long regionId);

    @Query(value = "SELECT COUNT(DISTINCT dem.programme_id) FROM {h-schema}demandes_aides dem " +
                   "JOIN {h-schema}agriculteurs a ON dem.agriculteur_id = a.id " +
                   "JOIN {h-schema}districts d ON a.district_id = d.id " +
                   "WHERE d.region_id = :regionId", nativeQuery = true)
    long countProgrammesByRegion(@Param("regionId") Long regionId);

    @Query(value = "SELECT r.nom AS nom_region, COUNT(a.id) AS total_actifs " +
                   "FROM {h-schema}regions r " +
                   "LEFT JOIN {h-schema}districts d ON d.region_id = r.id " +
                   "LEFT JOIN {h-schema}agriculteurs a ON a.district_id = d.id AND a.actif = true " +
                   "WHERE r.id = :regionId " +
                   "GROUP BY r.nom", nativeQuery = true)
    List<Object[]> getAgriculteursActifsParRegionUnique(@Param("regionId") Long regionId);
}