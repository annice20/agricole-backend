package com.agriculture.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.agriculture.backend.dto.SuiviProgrammeDTO;
import com.agriculture.backend.model.ProgrammeAide;

public interface ProgrammeAideRepository extends JpaRepository<ProgrammeAide, Long> {
	
	@Query("SELECT new com.agriculture.backend.dto.SuiviProgrammeDTO(" +
		       "p.id, p.titre, p.budget, COALESCE(SUM(f.montant), 0)) " +
		       "FROM ProgrammeAide p LEFT JOIN FinancementAgricole f ON f.programme = p " +
		       "GROUP BY p.id, p.titre, p.budget")
	List<SuiviProgrammeDTO> getSuiviParProgramme();
	long countByActifTrue();
}