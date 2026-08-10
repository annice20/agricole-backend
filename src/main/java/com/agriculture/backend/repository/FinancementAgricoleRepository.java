package com.agriculture.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.agriculture.backend.dto.RepartitionOrganismeDTO;
import com.agriculture.backend.model.FinancementAgricole;

public interface FinancementAgricoleRepository extends JpaRepository<FinancementAgricole, Long> {
	
	@Query("SELECT new com.agriculture.backend.dto.RepartitionOrganismeDTO(" +
		       "f.organisme, COUNT(f), COALESCE(SUM(f.montant), 0)) " +
		       "FROM FinancementAgricole f " +
		       "GROUP BY f.organisme")
		List<RepartitionOrganismeDTO> getRepartitionParOrganisme();

		List<FinancementAgricole> findAllByOrderByDateFinancementAsc();
}
