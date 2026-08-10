package com.agriculture.backend.repository;
import com.agriculture.backend.dto.RepartitionTypeAideDTO;
import com.agriculture.backend.model.DistributionAide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DistributionAideRepository extends JpaRepository<DistributionAide, Long> {
    List<DistributionAide> findByDemandeAideAgriculteurId(Long agriculteurId);

    @Query("SELECT new com.agriculture.backend.dto.RepartitionTypeAideDTO(" +
           "d.demandeAide.programme.typeAide, COUNT(d), COALESCE(SUM(d.montant), 0)) " +
           "FROM DistributionAide d " +
           "GROUP BY d.demandeAide.programme.typeAide")
    List<RepartitionTypeAideDTO> getRepartitionParType();

    // distributions filtrées par région (vue RESPONSABLE_REGIONAL)
    @Query("SELECT d FROM DistributionAide d " +
           "WHERE d.demandeAide.agriculteur.district.region.id = :regionId")
    List<DistributionAide> findByRegionId(@Param("regionId") Long regionId);
}