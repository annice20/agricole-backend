package com.agriculture.backend.repository;
import com.agriculture.backend.model.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
    List<Reclamation> findByAgriculteurId(Long agriculteurId);

    @Query("SELECT r FROM Reclamation r WHERE r.agriculteur.district.region.id = :regionId")
    List<Reclamation> findByRegionId(@Param("regionId") Long regionId);
}