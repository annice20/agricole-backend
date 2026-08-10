package com.agriculture.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.agriculture.backend.dto.RepartitionStatutDTO;
import com.agriculture.backend.model.DemandeAide;

public interface DemandeAideRepository extends JpaRepository<DemandeAide, Long> {
	
	@Query("SELECT new com.agriculture.backend.dto.RepartitionStatutDTO(" +
	       "d.statut, COUNT(d)) " +
	       "FROM DemandeAide d " +
	       "GROUP BY d.statut")
	List<RepartitionStatutDTO> getRepartitionParStatut();

	@Query("SELECT d FROM DemandeAide d WHERE d.agriculteur.district.region.id = :regionId")
	List<DemandeAide> findByRegionId(@Param("regionId") Long regionId);
}
