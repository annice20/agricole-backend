package com.agriculture.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.agriculture.backend.model.Agriculteur;
import com.agriculture.backend.model.Utilisateur;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgriculteurRepository extends JpaRepository<Agriculteur, Long> {

    // Tous les agriculteurs avec coordonnées GPS
    @Query(value = """
        SELECT a.id, a.nom, a.prenom, a.type_culture, a.superficie,
               a.adresse, a.actif,
               ST_Y(a.geom::geometry) AS latitude,
               ST_X(a.geom::geometry) AS longitude,
               d.nom AS nom_district,
               r.nom AS nom_region
        FROM agriculteurs a
        LEFT JOIN districts d ON a.district_id = d.id
        LEFT JOIN regions r ON d.region_id = r.id
        WHERE a.geom IS NOT NULL
        """, nativeQuery = true)
    List<Object[]> findAllAvecCoordonnees();

    // Bénéficiaires actifs uniquement avec coordonnées
    @Query(value = """
        SELECT a.id, a.nom, a.prenom, a.type_culture, a.superficie,
               a.adresse, a.actif,
               ST_Y(a.geom::geometry) AS latitude,
               ST_X(a.geom::geometry) AS longitude,
               d.nom AS nom_district,
               r.nom AS nom_region
        FROM agriculteurs a
        LEFT JOIN districts d ON a.district_id = d.id
        LEFT JOIN regions r ON d.region_id = r.id
        WHERE a.geom IS NOT NULL AND a.actif = true
        """, nativeQuery = true)
    List<Object[]> findBeneficiairesAvecCoordonnees();

    // Mise à jour de la géolocalisation d'un agriculteur
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE agriculteurs
        SET geom = ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
        WHERE id = :id
        """, nativeQuery = true)
    void updateGeolocalisation(@Param("id") Long id,
                                @Param("latitude") Double latitude,
                                @Param("longitude") Double longitude);

    // Statistiques par région pour analyse géographique
    @Query(value = """
        SELECT r.nom AS nom_region,
               COUNT(a.id) AS total,
               COUNT(CASE WHEN a.actif = true THEN 1 END) AS total_actifs,
               COALESCE(SUM(a.superficie), 0) AS superficie_totale,
               COUNT(CASE WHEN a.geom IS NOT NULL THEN 1 END) AS total_localises
        FROM regions r
        LEFT JOIN districts d ON d.region_id = r.id
        LEFT JOIN agriculteurs a ON a.district_id = d.id
        GROUP BY r.nom
        ORDER BY total_actifs DESC
        """, nativeQuery = true)
    List<Object[]> getStatistiquesParRegion();
    
    @Query("""
    	    SELECT DISTINCT a
    	    FROM Agriculteur a
    	    JOIN DemandeAide d ON d.agriculteur.id = a.id
    	    JOIN DistributionAide da ON da.demandeAide.id = d.id
    	    WHERE a.district.region.id = :regionId
    	""")
    List<Agriculteur> findBeneficiairesParRegion(Long regionId);
    
    Optional<Agriculteur> findByUtilisateur(Utilisateur utilisateur);
    long countByActifTrue();
    
    @Query("SELECT a FROM Agriculteur a WHERE a.district.region.id = :regionId")
    List<Agriculteur> findByRegionId(@Param("regionId") Long regionId);
}